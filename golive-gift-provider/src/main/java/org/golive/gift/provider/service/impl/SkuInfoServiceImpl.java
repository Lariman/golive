package org.golive.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.golive.common.interfaces.enums.CommonStatusEum;
import org.golive.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.golive.gift.provider.dao.mapper.SkuInfoMapper;
import org.golive.gift.provider.dao.po.SkuInfoPO;
import org.golive.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class SkuInfoServiceImpl implements ISkuInfoService {

    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public List<SkuInfoPO> queryBySkuIds(List<Long> skuIdList) {
        LambdaQueryWrapper<SkuInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(SkuInfoPO::getSkuId, skuIdList);
        queryWrapper.eq(SkuInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return skuInfoMapper.selectList(queryWrapper);
    }

    @Override
    public SkuInfoPO queryBySkuId(Long skuId) {
        LambdaQueryWrapper<SkuInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SkuInfoPO::getSkuId, skuId);
        queryWrapper.eq(SkuInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.last("limit 1");
        return skuInfoMapper.selectOne(queryWrapper);
    }

    @Override
    public SkuInfoPO queryBySkuIdFromCache(Long skuId) {
        String cacheKey = cacheKeyBuilder.buildSkuDetailKey(skuId);
        Object skuInfoCacheObj = redisTemplate.opsForValue().get(cacheKey);
        if(skuInfoCacheObj != null){
            return (SkuInfoPO) skuInfoCacheObj;
        }
        SkuInfoPO skuInfoPO = this.queryBySkuId(skuId);
        if(skuInfoPO == null){
            return null;
        }
        redisTemplate.opsForValue().set(cacheKey, skuInfoPO, 1, TimeUnit.DAYS);
        return skuInfoPO;
    }
}
