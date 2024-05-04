package org.golive.bank.api.service.impl;

import com.alibaba.fastjson.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.bank.api.service.IPayNotifyService;
import org.golive.bank.api.vo.WxPayNotifyVO;
import org.golive.bank.dto.PayOrderDTO;
import org.golive.bank.interfaces.IPayOrderRpc;
import org.springframework.stereotype.Service;

@Service
public class PayNotifyServiceImpl implements IPayNotifyService {

    @DubboReference
    private IPayOrderRpc payOrderRpc;

    @Override
    public String notifyHandler(String paramJson) {
        WxPayNotifyVO wxPayNotifyVO = JSON.parseObject(paramJson, WxPayNotifyVO.class);
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setUserId(wxPayNotifyVO.getUserId());
        payOrderDTO.setBizCode(wxPayNotifyVO.getBizCode());
        payOrderDTO.setOrderId(wxPayNotifyVO.getOrderId());
        return payOrderRpc.payNotify(payOrderDTO) ? "success" : "fail";
    }
}
