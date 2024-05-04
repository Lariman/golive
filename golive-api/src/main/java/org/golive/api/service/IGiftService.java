package org.golive.api.service;

import org.golive.api.vo.req.GiftReqVO;
import org.golive.api.vo.resp.GiftConfigVO;

import java.util.List;

public interface IGiftService {

    /**
     * 展示礼物列表
     */
    List<GiftConfigVO> listGift();

    /**
     * 送礼
     * @param giftReqVO
     * @return
     */
    boolean send(GiftReqVO giftReqVO);
}
