package org.golive.api.controller;

import jakarta.annotation.Resource;
import org.golive.api.error.GoliveApiError;
import org.golive.api.service.ILivingRoomService;
import org.golive.api.vo.LivingRoomInitVO;
import org.golive.api.vo.req.LivingRoomReqVO;
import org.golive.api.vo.req.OnlinePkReqVO;
import org.golive.common.interfaces.vo.WebResponseVO;
import org.golive.web.starter.context.GoliveRequestContext;
import org.golive.web.starter.error.BizBaseErrorEnum;
import org.golive.web.starter.error.ErrorAssert;
import org.golive.web.starter.limit.RequestLimit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/living")
public class LivingRoomController {

    @Resource
    private ILivingRoomService livingRoomService;

    @PostMapping("/prepareRedPacket")
    @RequestLimit(limit = 1, second = 10, msg = "正在初始化中,请稍等")
    public WebResponseVO prepareRedPacket(LivingRoomReqVO livingRoomReqVO){
        return WebResponseVO.success(livingRoomService.prepareRedPacket(GoliveRequestContext.getUserId(), livingRoomReqVO.getRoomId()));
    }

    @PostMapping("/startRedPacket")
    @RequestLimit(limit = 1, second = 10, msg = "正在广播直播间用户,请稍等")
    public WebResponseVO startRedPacket(LivingRoomReqVO livingRoomReqVO){
        return WebResponseVO.success(livingRoomService.startRedPacket(GoliveRequestContext.getUserId(), livingRoomReqVO.getRedPacketConfigCode()));
    }

    @PostMapping("/getRedPacket")
    @RequestLimit(limit = 1, second = 7, msg = "")
    public WebResponseVO getRedPacket(LivingRoomReqVO livingRoomReqVO){
        return WebResponseVO.success(livingRoomService.getRedPacket(GoliveRequestContext.getUserId(), livingRoomReqVO.getRedPacketConfigCode()));
    }

    @PostMapping("/list")
    public WebResponseVO list(LivingRoomReqVO livingRoomReqVO){
        ErrorAssert.isTrue(livingRoomReqVO == null || livingRoomReqVO.getType() == null, GoliveApiError.GOLIVE_ROOM_TYPE_MISSING);
        ErrorAssert.isTrue(livingRoomReqVO.getPage() > 0 && livingRoomReqVO.getPageSize() <= 100, BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.list(livingRoomReqVO));
    }

    @RequestLimit(limit = 1, second = 10, msg = "开播请求过于频繁,请稍后再试")
    @PostMapping("/startingLiving")
    public WebResponseVO startingLiving(Integer type){
        // 调用rpc,往开播表t_living_room写入一条记录
        ErrorAssert.isNotNull(type, BizBaseErrorEnum.PARAM_ERROR);
        Integer roomId = livingRoomService.startingLiving(type);
        LivingRoomInitVO initVO = new LivingRoomInitVO();
        initVO.setRoomId(roomId);
        return WebResponseVO.success(initVO);
    }

    /**
     * 用户连线pk
     * @param onlinePkReqVO
     * @return
     */
    @PostMapping("/onlinePk")
    @RequestLimit(limit = 1, second = 3)
    public WebResponseVO onlinePk(OnlinePkReqVO onlinePkReqVO){
        ErrorAssert.isNotNull(onlinePkReqVO.getRoomId(), BizBaseErrorEnum.PARAM_ERROR);
        return WebResponseVO.success(livingRoomService.onlinePk(onlinePkReqVO));
    }

    @RequestLimit(limit = 1, second = 10, msg = "关播请求过于频繁,请稍后再试")
    @PostMapping("/closeLiving")
    public WebResponseVO closeLiving(Integer roomId){
        ErrorAssert.isNotNull(roomId, BizBaseErrorEnum.PARAM_ERROR);
        boolean closeStatus = livingRoomService.closeLiving(roomId);
        if(closeStatus){
            return WebResponseVO.success();
        }
        return WebResponseVO.bizError("关播异常");
    }

    /**
     * 获取主播相关配置信息(只有主播才会有权限)
     */
    @PostMapping("/anchorConfig")
    public WebResponseVO anchorConfig(Integer roomId){
        long userId = GoliveRequestContext.getUserId();
        return WebResponseVO.success(livingRoomService.anchorConfig(userId, roomId));
    }
}
