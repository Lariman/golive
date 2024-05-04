package org.golive.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.bank.dto.PayOrderDTO;
import org.golive.bank.interfaces.IPayOrderRpc;
import org.golive.bank.provider.dao.po.PayOrderPO;
import org.golive.bank.provider.service.IPayOrderService;
import org.golive.common.interfaces.utils.ConvertBeanUtils;

@DubboService
public class PayOrderRpcImpl implements IPayOrderRpc {

    @Resource
    private IPayOrderService payOrderService;

    @Override
    public String insertOne(PayOrderDTO payOrderDTO) {
        return payOrderService.insertOne(ConvertBeanUtils.convert(payOrderDTO, PayOrderPO.class));
    }

    @Override
    public boolean updateOrderStatus(String orderId, Integer status) {
        return payOrderService.updateOrderStatus(orderId, status);
    }

    @Override
    public boolean updateOrderStatus(Long id, Integer status) {
        return payOrderService.updateOrderStatus(id, status);
    }

    @Override
    public boolean payNotify(PayOrderDTO payOrderDTO) {
        return payOrderService.payNotify(payOrderDTO);
    }
}
