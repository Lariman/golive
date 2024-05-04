package org.golive.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.golive.gift.dto.SkuDetailInfoDTO;
import org.golive.gift.dto.SkuInfoDTO;
import org.golive.gift.interfaces.ISkuInfoRpc;
import org.golive.gift.provider.dao.po.SkuInfoPO;
import org.golive.gift.provider.service.IAnchorShopInfoService;
import org.golive.gift.provider.service.ISkuInfoService;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@DubboService
public class SkuInfoRpcImpl implements ISkuInfoRpc {

    @Resource
    private ISkuInfoService skuInfoService;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;


    @Override
    public List<SkuInfoDTO> queryByAnchorId(Long anchorId) {
        List<Long> skuIdList = anchorShopInfoService.querySkuIdByAnchorId(anchorId);
        List<SkuInfoPO> skuInfoPOS = skuInfoService.queryBySkuIds(skuIdList);
        return ConvertBeanUtils.convertList(skuInfoPOS, SkuInfoDTO.class);
    }

    @Override
    public SkuDetailInfoDTO queryBySkuId(Long skuId) {
        return ConvertBeanUtils.convert(skuInfoService.queryBySkuIdFromCache(skuId), SkuDetailInfoDTO.class);
    }
}
