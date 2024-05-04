package org.golive.bank.provider.service;

import org.golive.bank.dto.PayOrderDTO;
import org.golive.bank.provider.dao.po.PayOrderPO;

public interface IPayOrderService {

    /**
     * 根据订单id查询
     * @param orderId
     * @return
     */
    PayOrderPO queryByOrderId(String orderId);

    /**
     * 插入订单
     * @param payOrderPO
     * @return
     */
    String insertOne(PayOrderPO payOrderPO);

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
