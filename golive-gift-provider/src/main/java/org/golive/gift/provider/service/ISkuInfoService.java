package org.golive.gift.provider.service;

import org.golive.gift.provider.dao.po.SkuInfoPO;

import java.util.List;

public interface ISkuInfoService {

    /**
     * 批量skuId查询
     *
     * @param skuIdList
     * @return
     */
    List<SkuInfoPO> queryBySkuIds(List<Long>skuIdList);

    /**
     * 查询商品详情
     * @param skuId
     * @return
     */
    SkuInfoPO queryBySkuId(Long skuId);

    /**
     * 查询商品详情
     * @param skuId
     * @return
     */
    SkuInfoPO queryBySkuIdFromCache(Long skuId);

}
