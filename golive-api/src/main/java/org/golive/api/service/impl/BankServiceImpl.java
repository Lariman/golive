package org.golive.api.service.impl;

import com.alibaba.fastjson2.JSON;
import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.api.service.IBankService;
import org.golive.api.vo.req.PayProductReqVO;
import org.golive.api.vo.resp.PayProductItemVO;
import org.golive.api.vo.resp.PayProductRespVO;
import org.golive.api.vo.resp.PayProductVO;
import org.golive.bank.constants.OrderStatusEnum;
import org.golive.bank.constants.PayChannelEnum;
import org.golive.bank.dto.PayOrderDTO;
import org.golive.bank.dto.PayProductDTO;
import org.golive.bank.interfaces.IGoliveCurrencyAccountRpc;
import org.golive.bank.interfaces.IPayOrderRpc;
import org.golive.bank.interfaces.IPayProductRpc;
import org.golive.bank.constants.PaySourceEnum;
import org.golive.web.starter.context.GoliveRequestContext;
import org.golive.web.starter.error.BizBaseErrorEnum;
import org.golive.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BankServiceImpl implements IBankService {

    @DubboReference
    private IPayProductRpc payProductRpc;
    @DubboReference
    private IGoliveCurrencyAccountRpc goliveCurrencyAccountRpc;
    @DubboReference
    private IPayOrderRpc payOrderRpc;

    @Override
    public PayProductVO products(Integer type) {
        List<PayProductDTO> payProductDTOS = payProductRpc.products(type);
        PayProductVO payProductVO = new PayProductVO();
        List<PayProductItemVO> itemList = new ArrayList<>();
        for (PayProductDTO payProductDTO : payProductDTOS) {
            PayProductItemVO itemVO = new PayProductItemVO();
            itemVO.setName(payProductDTO.getName());
            itemVO.setId(payProductDTO.getId());
            itemVO.setCoinNum(JSON.parseObject(payProductDTO.getExtra()).getInteger("coin"));
            itemList.add(itemVO);
        }
        payProductVO.setPayProductItemVOList(itemList);
        payProductVO.setCurrentBalance(Optional.ofNullable(goliveCurrencyAccountRpc.getBalance(GoliveRequestContext.getUserId())).orElse(0));
        return payProductVO;
    }

    @Override
    public PayProductRespVO payProduct(PayProductReqVO payProductReqVO) {
        // 参数校验
        ErrorAssert.isTrue(payProductReqVO != null &&payProductReqVO.getProductId() != null && payProductReqVO.getPaySource() != null, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isNotNull(PaySourceEnum.find(payProductReqVO.getPaySource()), BizBaseErrorEnum.PARAM_ERROR);
        PayProductDTO payProductDTO = payProductRpc.getByProductId(payProductReqVO.getProductId());
        ErrorAssert.isNotNull(payProductDTO, BizBaseErrorEnum.PARAM_ERROR);

        // 插入一条订单,待支付状态
        PayOrderDTO payOrderDTO = new PayOrderDTO();
        payOrderDTO.setProductId(payProductReqVO.getProductId());
        payOrderDTO.setUserId(GoliveRequestContext.getUserId());
        payOrderDTO.setSource(payProductReqVO.getPaySource());
        payOrderDTO.setPayChannel(payProductReqVO.getPayChannel());
        String orderId = payOrderRpc.insertOne(payOrderDTO);

        // 更新订单为支付中状态
        payOrderRpc.updateOrderStatus(orderId, OrderStatusEnum.PAYING.getCode());
        PayProductRespVO payProductRespVO = new PayProductRespVO();
        payProductRespVO.setOrderId(orderId);

        // todo 远程http请求 resttemplate->支付回调接口
        return payProductRespVO;
    }
}
