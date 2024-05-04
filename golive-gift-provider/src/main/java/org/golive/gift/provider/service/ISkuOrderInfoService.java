package org.golive.gift.provider.service;

import org.golive.gift.dto.SkuOrderInfoReqDTO;
import org.golive.gift.dto.SkuOrderInfoRespDTO;
import org.golive.gift.provider.dao.po.SkuOrderInfoPO;

public interface ISkuOrderInfoService {

    /**
     * 支持多直播间内用户下单的订单查询
     * @param userId
     * @param roomId
     * @return
     */
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);

    /**
     * 根据订单id查询
     * @param orderId
     * @return
     */
    SkuOrderInfoRespDTO queryByOrderId(Long orderId);

    /**
     * 插入一条订单信息
     * @param skuOrderInfoReqDTO
     * @return
     */
    SkuOrderInfoPO insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 根据订单id修改状态
     * @param skuOrderInfoReqDTO
     * @return
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO);
}
