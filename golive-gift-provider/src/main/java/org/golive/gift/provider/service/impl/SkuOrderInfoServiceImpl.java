package org.golive.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.apache.commons.lang3.StringUtils;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.golive.gift.dto.SkuOrderInfoReqDTO;
import org.golive.gift.dto.SkuOrderInfoRespDTO;
import org.golive.gift.provider.dao.mapper.SkuOrderInfoMapper;
import org.golive.gift.provider.dao.po.SkuOrderInfoPO;
import org.golive.gift.provider.service.ISkuOrderInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SkuOrderInfoServiceImpl implements ISkuOrderInfoService {

    @Resource
    private SkuOrderInfoMapper skuOrderInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        String cacheKey = cacheKeyBuilder.buildSkuOrder(userId, roomId);
        Object cacheObj = redisTemplate.opsForValue().get(cacheKey);
        if(cacheObj != null){
            return ConvertBeanUtils.convert(cacheObj, SkuOrderInfoRespDTO.class);
        }
        LambdaQueryWrapper<SkuOrderInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuOrderInfoPO::getUserId, userId);
        queryWrapper.eq(SkuOrderInfoPO::getRoomId, roomId);
        queryWrapper.orderByDesc(SkuOrderInfoPO::getId);
        queryWrapper.last("limit 1");
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoMapper.selectOne(queryWrapper);
        if(skuOrderInfoPO != null){
            SkuOrderInfoRespDTO skuOrderInfoRespDTO = ConvertBeanUtils.convert(skuOrderInfoPO, SkuOrderInfoRespDTO.class);
            redisTemplate.opsForValue().set(cacheKey, skuOrderInfoRespDTO, 60, TimeUnit.MINUTES);
            return skuOrderInfoRespDTO;
        }
        return null;
    }

    @Override
    public SkuOrderInfoRespDTO queryByOrderId(Long orderId) {
        String cacheKey = cacheKeyBuilder.buildSkuOrderInfo(orderId);
        Object cacheObj = redisTemplate.opsForValue().get(cacheKey);
        if(cacheObj != null){
            return ConvertBeanUtils.convert(cacheObj, SkuOrderInfoRespDTO.class);
        }
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoMapper.selectById(orderId);
        if(skuOrderInfoPO != null){
            SkuOrderInfoRespDTO skuOrderInfoRespDTO = ConvertBeanUtils.convert(skuOrderInfoPO, SkuOrderInfoRespDTO.class);
            redisTemplate.opsForValue().set(cacheKey, skuOrderInfoRespDTO, 60, TimeUnit.MINUTES);
            return skuOrderInfoRespDTO;
        }
        return null;
    }

    @Override
    public SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        String skuIdListStr = StringUtils.join(skuOrderInfoReqDTO.getSkuIdList(), ",");
        SkuOrderInfoPO skuOrderInfoPO = ConvertBeanUtils.convert(skuOrderInfoReqDTO, SkuOrderInfoPO.class);
        skuOrderInfoPO.setSkuIdList(skuIdListStr);
        skuOrderInfoMapper.insert(skuOrderInfoPO);
        return skuOrderInfoPO;
    }

    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        SkuOrderInfoPO skuOrderInfoPO = new SkuOrderInfoPO();
        skuOrderInfoPO.setStatus(skuOrderInfoPO.getStatus());
        skuOrderInfoPO.setId(skuOrderInfoReqDTO.getId());
        skuOrderInfoMapper.updateById(skuOrderInfoPO);
        String cacheKey = cacheKeyBuilder.buildSkuOrder(skuOrderInfoReqDTO.getUserId(), skuOrderInfoReqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }
}
