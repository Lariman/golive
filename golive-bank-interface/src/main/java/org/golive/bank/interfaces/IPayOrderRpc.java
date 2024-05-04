package org.golive.bank.interfaces;

import org.golive.bank.dto.PayOrderDTO;

public interface IPayOrderRpc {

    /**
     * 插入订单
     * @param payOrderPO
     * @return
     */
    String insertOne(PayOrderDTO payOrderPO);

    /**
     * 根据订单id做更新操作
     */
    boolean updateOrderStatus(String orderId, Integer status);


    /**
     * 根据主键id做更新操作
     */
    boolean updateOrderStatus(Long id, Integer status);

    /**
     * 支付回调需要请求该接口
     * @param payOrderDTO
     * @return
     */
    boolean payNotify(PayOrderDTO payOrderDTO);
}
