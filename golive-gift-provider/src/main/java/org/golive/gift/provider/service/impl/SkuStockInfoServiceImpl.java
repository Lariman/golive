package org.golive.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.golive.common.interfaces.enums.CommonStatusEum;
import org.golive.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.golive.gift.constants.SkuOrderInfoEnum;
import org.golive.gift.dto.RollBackStockDTO;
import org.golive.gift.dto.SkuOrderInfoReqDTO;
import org.golive.gift.dto.SkuOrderInfoRespDTO;
import org.golive.gift.provider.dao.mapper.SkuStockInfoMapper;
import org.golive.gift.provider.dao.po.SkuStockInfoPO;
import org.golive.gift.provider.service.ISkuOrderInfoService;
import org.golive.gift.provider.service.ISkuStockInfoService;
import org.golive.gift.provider.service.bo.DecrStockNumBO;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SkuStockInfoServiceImpl implements ISkuStockInfoService {

    @Resource
    private SkuStockInfoMapper skuStockInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private ISkuOrderInfoService skuOrderInfoService;

    private String LUA_SCRIPT =
            "if(redis.call('exists', KEYS[1])) == 1 then" +
                    " local currentStock=redis.call('get',KEYS[1])" +
                    " if(tonumber(currentStock)>0 and tonumber(currentStock)-tonumber(ARGV[1])>=0) then " +
                    "     return redis.call('decrby',KEYS[1],tonumber(ARGV[1])) " +
                    " else return -1 end " +
                    " else " +
                    "return -1 end";


    @Override
    public boolean updateStockNum(Long skuId, Integer num) {
        SkuStockInfoPO skuStockInfoPO = new SkuStockInfoPO();
        skuStockInfoPO.setStockNum(num);
        LambdaQueryWrapper<SkuStockInfoPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SkuStockInfoPO::getSkuId, skuId);
        skuStockInfoMapper.update(skuStockInfoPO, lambdaQueryWrapper);
        return true;
    }

    @Override
    public DecrStockNumBO decrStockNumBySkuId(Long skuId, Integer num) {
        SkuStockInfoPO skuStockInfoPO = this.queryBySkuId(skuId);
        DecrStockNumBO decrStockNumBO = new DecrStockNumBO();
        if(skuStockInfoPO.getStockNum() == 0 || skuStockInfoPO.getStockNum() - num < 0){
            decrStockNumBO.setNoStock(true);
            decrStockNumBO.setSuccess(false);
            return decrStockNumBO;
        }
        decrStockNumBO.setNoStock(false);
        boolean updateStatus = skuStockInfoMapper.decrStockNumBySkuId(skuId, num, skuStockInfoPO.getVersion()) > 0;
        decrStockNumBO.setSuccess(updateStatus);
        return decrStockNumBO;
    }

    @Override
    public boolean decrStockNumBySkuIdV2(Long skuId, Integer num) {
        // 线程安全:如果直接使用redis命令操作的化,可能会有多元请求,可能导致超卖,线程不安全!
        // 使用lua方案去替代,进行改良
        // 1.根据skuId查询库存信息,从缓存好的redis中去取库存信息 (IO)
        // 判断:sku库存值>0,sku库存值 - num > 0  (其他线程也在这么操作)
        // 扣减 decrby (IO)

        // DefaultRedisScript用于传输lua脚本, 泛型代表lua脚本执行后返回类型
        DefaultRedisScript<Long> redisScript = new DefaultRedisScript<>();
        redisScript.setScriptText(LUA_SCRIPT);
        redisScript.setResultType(Long.class);
        String skuStockCacheKey = cacheKeyBuilder.buildSkuStock(skuId);
        return redisTemplate.execute(redisScript, Collections.singletonList(skuStockCacheKey), num) >= 0;
    }

    @Override
    public SkuStockInfoPO queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuStockInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuStockInfoPO::getSkuId, skuId);
        queryWrapper.eq(SkuStockInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return skuStockInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public List<SkuStockInfoPO> queryBySkuIds(List<Long> skuIdList) {
        LambdaQueryWrapper<SkuStockInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuStockInfoPO::getSkuId, skuIdList);
        queryWrapper.eq(SkuStockInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return skuStockInfoMapper.selectList(queryWrapper);
    }

    @Override
    public void stockRollBackHandler(RollBackStockDTO rollBackStockDTO) {
        SkuOrderInfoRespDTO orderInfoRespDTO = skuOrderInfoService.queryByOrderId(rollBackStockDTO.getOrderId());
        if(orderInfoRespDTO == null || SkuOrderInfoEnum.HAS_PAY.getCode().equals(orderInfoRespDTO.getStatus())){
            return;
        }
        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setStatus(SkuOrderInfoEnum.END.getCode());
        skuOrderInfoReqDTO.setId(orderInfoRespDTO.getId());
        // 状态更新
        skuOrderInfoService.updateOrderStatus(skuOrderInfoReqDTO);
        // 库存回滚
        List<Long> skuIdList = Arrays.asList(orderInfoRespDTO.getSkuIdList().split(",")).stream().map(x->Long.valueOf(x)).collect(Collectors.toList());
        skuIdList.parallelStream().forEach(skuId->{
            String skuStockCacheKey = cacheKeyBuilder.buildSkuStock(skuId);
            redisTemplate.opsForValue().increment(skuStockCacheKey, 1);
        });

    }
}
