package org.golive.api.service;

import org.golive.api.vo.req.PayProductReqVO;
import org.golive.api.vo.resp.PayProductRespVO;
import org.golive.api.vo.resp.PayProductVO;
import org.golive.bank.dto.PayProductDTO;

import java.util.List;

public interface IBankService {

    /**
     * 查询相关的产品列表信息
     * @param type
     * @return
     */
    PayProductVO products(Integer type);

    /**
     * 发起支付
     * @param payProductReqVO
     * @return
     */
    PayProductRespVO payProduct(PayProductReqVO payProductReqVO);
}
