package org.golive.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.golive.gift.dto.ShopCarItemRespDTO;
import org.golive.gift.dto.ShopCarReqDTO;
import org.golive.gift.dto.ShopCarRespDTO;
import org.golive.gift.dto.SkuInfoDTO;
import org.golive.gift.provider.dao.po.SkuInfoPO;
import org.golive.gift.provider.service.IShopCarService;
import org.golive.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ShopCarServiceImpl implements IShopCarService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private ISkuInfoService skuInfoService;

    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        Cursor<Map.Entry<Object, Object>> allCarData = redisTemplate.opsForHash().scan(cacheKey, ScanOptions.scanOptions().match("*").build());
        List<ShopCarItemRespDTO> shopCarItemRespDTOS = new ArrayList<>();
        Map<Long, Integer> skuCountMap = new HashMap<>();
        while(allCarData.hasNext()){
            Map.Entry<Object, Object> entry = allCarData.next();
            skuCountMap.put((Long) entry.getKey(), (Integer) entry.getValue());
        }
        List<SkuInfoPO> skuInfoPOList = skuInfoService.queryBySkuIds(new ArrayList<>(skuCountMap.keySet()));
        for (SkuInfoPO skuInfoPO : skuInfoPOList) {
            SkuInfoDTO skuInfoDTO = ConvertBeanUtils.convert(skuInfoPO, SkuInfoDTO.class);
            Integer count = skuCountMap.get(skuInfoDTO.getSkuId());
            shopCarItemRespDTOS.add(new ShopCarItemRespDTO(count, skuInfoDTO));
        }
        ShopCarRespDTO shopCarRespDTO = new ShopCarRespDTO();
        shopCarRespDTO.setRoomId(shopCarReqDTO.getRoomId());
        shopCarRespDTO.setUserId(shopCarReqDTO.getUserId());
        shopCarRespDTO.setShopCarItemRespDTOS(shopCarItemRespDTOS);
        return shopCarRespDTO;
    }

    @Override
    public Boolean addCar(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        // 一个购物车 -> 多个商品
        // 读取所有商品的数据
        // 每个商品都有数量(目前的业务场景中没有体现)
        // 1.String(存对象,对象里面关联上商品的数据信息)
        // 2.set / list
        // 3.map(k,v) key是skuId,value是商品的数量
        redisTemplate.opsForHash().put(cacheKey, shopCarReqDTO.getSkuId(), 1);  // 用map来存购物车中的商品,key为skuId,值为数量
        return true;
    }

    @Override
    public Boolean removeFromCar(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        redisTemplate.opsForHash().delete(cacheKey, shopCarReqDTO.getSkuId());
        return true;
    }

    @Override
    public Boolean clearShopCar(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        redisTemplate.delete(cacheKey);
        return true;
    }

    @Override
    public Boolean addCarItemNum(ShopCarReqDTO shopCarReqDTO) {
        String cacheKey = cacheKeyBuilder.buildUserShopCar(shopCarReqDTO.getUserId(), shopCarReqDTO.getRoomId());
        redisTemplate.opsForHash().increment(cacheKey, shopCarReqDTO.getSkuId(), 1);
        return true;
    }
}
