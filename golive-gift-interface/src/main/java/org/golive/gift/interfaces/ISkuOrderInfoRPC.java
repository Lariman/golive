package org.golive.gift.interfaces;

import org.golive.gift.dto.*;

public interface ISkuOrderInfoRPC {

    /**
     * 支持多直播间内用户下单的订单查询
     * @param userId
     * @param roomId
     * @return
     */
    SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId);

    /**
     * 插入一条订单信息
     * @param skuOrderInfoReqDTO
     * @return
     */
    boolean insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 根据订单id修改状态
     * @param skuOrderInfoReqDTO
     * @return
     */
    boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO);

    /**
     * 预支付订单生成
     *
     * @param prepareOrderReqDTO
     * @return
     */
    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO prepareOrderReqDTO);

    boolean payNow(PayNowReqDTO payNowReqDTO);
}
