package org.golive.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.golive.gift.interfaces.ISkuStockInfoRPC;
import org.golive.gift.provider.dao.po.SkuStockInfoPO;
import org.golive.gift.provider.service.IAnchorShopInfoService;
import org.golive.gift.provider.service.ISkuStockInfoService;
import org.golive.gift.provider.service.bo.DecrStockNumBO;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@DubboService
public class SkuStockInfoRPCImpl implements ISkuStockInfoRPC {

    @Resource
    private ISkuStockInfoService stockInfoService;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    private final int MAX_TRY_TIMES = 5;

    @Override
    public boolean decrStockNumBySkuId(Long skuId, Integer num) {
        for(int i = 0; i < MAX_TRY_TIMES; i++){
            DecrStockNumBO decrStockNumBO = stockInfoService.decrStockNumBySkuId(skuId, num);
            if(decrStockNumBO.isNoStock()){  // 库存不足
                return false;
            } else if(decrStockNumBO.isSuccess()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean decrStockNumBySkuIdV2(Long skuId, Integer num) {
        return stockInfoService.decrStockNumBySkuIdV2(skuId, num);
    }

    @Override
    public boolean prepareStockInfo(Long anchorId) {
        List<Long> skuIdList = anchorShopInfoService.querySkuIdByAnchorId(anchorId);
        List<SkuStockInfoPO> skuStockInfoPOS = stockInfoService.queryBySkuIds(skuIdList);
        // 通常来说一个主播带货数量不会很多,可能就几个货物,更合适得做法是使用multiset
        Map<String, Integer> saveCacheMap = skuStockInfoPOS.stream().collect(Collectors.toMap(skuStockInfoPO -> cacheKeyBuilder.buildSkuStock(skuStockInfoPO.getSkuId()), x -> x.getStockNum()));
        redisTemplate.opsForValue().multiSet(saveCacheMap);
        // 对命令执行批量过期时间设置
        redisTemplate.executePipelined(new SessionCallback<Object>() {
            @Override
            public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                for (String redisKey : saveCacheMap.keySet()) {
                    operations.expire((K) redisKey, 1, TimeUnit.DAYS);
                }
                return null;
            }
        });
        // for (SkuStockInfoPO skuStockInfoPO : skuStockInfoPOS) {
        //     String cacheKey = cacheKeyBuilder.buildSkuStock(skuStockInfoPO.getSkuId());
        //     redisTemplate.opsForValue().set(cacheKey, skuStockInfoPO.getStockNum(), 1, TimeUnit.DAYS);
        // }
        return true;
    }

    @Override
    public Integer queryStockNum(Long skuId) {
        String cacheKey = cacheKeyBuilder.buildSkuStock(skuId);
        Object stockNumObj = redisTemplate.opsForValue().get(cacheKey);
        return stockNumObj == null ? null : (Integer) stockNumObj;
    }

    @Override
    public boolean syncStockNumToMySQL(Long anchorId) {
        List<Long> skuIdList = anchorShopInfoService.querySkuIdByAnchorId(anchorId);
        for (Long skuId : skuIdList) {
            Integer stockNum = this.queryStockNum(skuId);
            if(stockNum != null){
                stockInfoService.updateStockNum(skuId, stockNum);
            }
        }
        return true;
    }
}
