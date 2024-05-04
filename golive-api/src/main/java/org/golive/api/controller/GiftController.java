package org.golive.api.controller;

import jakarta.annotation.Resource;
import org.golive.api.service.IGiftService;
import org.golive.api.vo.req.GiftReqVO;
import org.golive.api.vo.resp.GiftConfigVO;
import org.golive.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/gift")
public class GiftController {

    @Resource
    private IGiftService giftService;

    /**
     * 获取礼物列表
     */
    @PostMapping("/listGift")
    public WebResponseVO listGift(){
        // 调用rpc的方法,检索出来礼物配置列表
        List<GiftConfigVO> giftConfigVOS = giftService.listGift();
        return WebResponseVO.success(giftConfigVOS);
    }

    /**
     * 发送礼物方法
     */
    @PostMapping("/send")
    public WebResponseVO send(GiftReqVO giftReqVO) {
        return WebResponseVO.success(giftService.send(giftReqVO));
    }

}
