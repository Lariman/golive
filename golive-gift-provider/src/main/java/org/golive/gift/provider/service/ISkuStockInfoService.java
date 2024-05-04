package org.golive.gift.provider.service;

import org.golive.gift.dto.RollBackStockDTO;
import org.golive.gift.provider.dao.po.SkuStockInfoPO;
import org.golive.gift.provider.service.bo.DecrStockNumBO;

import java.util.List;

public interface ISkuStockInfoService {

    /**
     * 更新库存
     * @param skuId
     * @param num
     * @return
     */
    boolean updateStockNum(Long skuId, Integer num);

    /**
     * 根据skuId更新库存值
     * @param skuId
     * @return
     */
    DecrStockNumBO decrStockNumBySkuId(Long skuId, Integer num);

    /**
     * 根据skuId扣减库存值
     * @param skuId
     * @param num
     * @return
     */
    boolean decrStockNumBySkuIdV2(Long skuId, Integer num);

    /**
     * 根据skuId查询库存信息
     * @param skuId
     * @return
     */
    SkuStockInfoPO queryBySkuId(Long skuId);

    /**
     * 批量sku信息查询
     * @param skuIdList
     * @return
     */
    List<SkuStockInfoPO> queryBySkuIds(List<Long> skuIdList);

    /**
     * 处理库存回滚逻辑
     * @param rollBackStockDTO
     */
    void stockRollBackHandler(RollBackStockDTO rollBackStockDTO);
}
