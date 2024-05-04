package org.golive.api.service.impl;


import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.api.error.ApiErrorEnum;
import org.golive.api.service.ILivingRoomService;
import org.golive.api.vo.LivingRoomInitVO;
import org.golive.api.vo.req.LivingRoomReqVO;
import org.golive.api.vo.req.OnlinePkReqVO;
import org.golive.api.vo.resp.LivingRoomPageRespVO;
import org.golive.api.vo.resp.LivingRoomRespVO;
import org.golive.api.vo.resp.RedPacketReceiveVO;
import org.golive.common.interfaces.dto.PageWrapper;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.gift.dto.RedPacketConfigReqDTO;
import org.golive.gift.dto.RedPacketConfigRespDTO;
import org.golive.gift.dto.RedPacketReceiveDTO;
import org.golive.gift.interfaces.IRedPacketConfigRpc;
import org.golive.im.constants.AppIdEnum;
import org.golive.living.interfaces.dto.LivingPkRespDTO;
import org.golive.living.interfaces.dto.LivingRoomReqDTO;
import org.golive.living.interfaces.dto.LivingRoomRespDTO;
import org.golive.living.interfaces.rpc.ILivingRoomRpc;
import org.golive.user.dto.UserDTO;
import org.golive.user.interfaces.IUserRpc;
import org.golive.web.starter.context.GoliveRequestContext;
import org.golive.web.starter.error.BizBaseErrorEnum;
import org.golive.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author idea
 * @Date: Created in 21:15 2023/7/19
 * @Description
 */
@Service
public class LivingRoomServiceImpl implements ILivingRoomService {

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private IUserRpc userRpc;
    @DubboReference
    private IRedPacketConfigRpc redPacketConfigRpc;

    @Override
    public LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO) {
        PageWrapper<LivingRoomRespDTO> resultPage = livingRoomRpc.list(ConvertBeanUtils.convert(livingRoomReqVO, LivingRoomReqDTO.class));
        LivingRoomPageRespVO livingRoomPageRespVO = new LivingRoomPageRespVO();
        livingRoomPageRespVO.setList(ConvertBeanUtils.convertList(resultPage.getList(), LivingRoomRespVO.class));
        livingRoomPageRespVO.setHasNext(resultPage.isHasNext());
        return livingRoomPageRespVO;
    }

    @Override
    public Integer startingLiving(Integer type) {
        Long userId = GoliveRequestContext.getUserId();
        UserDTO userDTO = userRpc.getByUserId(userId);
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setAnchorId(userId);
        livingRoomReqDTO.setRoomName("主播-" + GoliveRequestContext.getUserId() + "的直播间");
        livingRoomReqDTO.setCovertImg(userDTO.getAvatar());
        livingRoomReqDTO.setType(type);
        return livingRoomRpc.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean onlinePk(OnlinePkReqVO onlinePkReqVO) {
        LivingRoomReqDTO reqDTO = ConvertBeanUtils.convert(onlinePkReqVO, LivingRoomReqDTO.class);
        reqDTO.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
        reqDTO.setPkObjId(GoliveRequestContext.getUserId());
        LivingPkRespDTO tryOnlineStatus = livingRoomRpc.onlinePk(reqDTO);
        ErrorAssert.isTrue(tryOnlineStatus.isOnlineStatus(), ApiErrorEnum.PK_ONLINE_BUSY);
        return true;
    }

    @Override
    public boolean closeLiving(Integer roomId) {
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(roomId);
        livingRoomReqDTO.setAnchorId(GoliveRequestContext.getUserId());
        return livingRoomRpc.closeLiving(livingRoomReqDTO);
    }

    @Override
    public LivingRoomInitVO anchorConfig(Long userId, Integer roomId) {
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(respDTO, ApiErrorEnum.LIVING_ROOM_END);
        Map<Long, UserDTO> userDTOMap = userRpc.batchQueryUserInfo(Arrays.asList(respDTO.getAnchorId(), userId).stream().distinct().collect(Collectors.toList()));
        UserDTO anchor = userDTOMap.get(respDTO.getAnchorId());
        UserDTO watcher = userDTOMap.get(userId);
        LivingRoomInitVO respVO = new LivingRoomInitVO();
        respVO.setAnchorNickName(anchor.getNickName());
        respVO.setWatcherNickName(watcher.getNickName());
        respVO.setUserId(userId);
        //给定一个默认的头像
        respVO.setAvatar(StringUtils.isEmpty(anchor.getAvatar()) ? "https://s1.ax1x.com/2022/12/18/zb6q6f.png" : anchor.getAvatar());
        respVO.setWatcherAvatar(watcher.getAvatar());
        respVO.setDefaultBgImg("https://picst.sunbangyan.cn/2023/08/29/waxzj0.png");
        if (respDTO == null || respDTO.getAnchorId() == null || userId == null) {
            //这种就是属于直播间已经不存在的情况了
            respVO.setRoomId(-1);
            return respVO;
        }
        boolean isAnchor = respDTO.getAnchorId().equals(userId);
        respVO.setRoomId(respDTO.getId());
        respVO.setAnchorId(respDTO.getAnchorId());
        respVO.setAnchor(isAnchor);
        if(isAnchor){
            RedPacketConfigRespDTO redPacketConfigRespDTO = redPacketConfigRpc.queryByAnchorId(userId);
            if(redPacketConfigRespDTO != null){
                respVO.setRedPacketConfigCode(redPacketConfigRespDTO.getConfigCode());
            }
        }

        return respVO;
    }

    @Override
    public Boolean prepareRedPacket(Long userId, Integer roomId) {
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(respDTO, BizBaseErrorEnum.PARAM_ERROR);
        ErrorAssert.isNotNull(respDTO.getAnchorId().equals(userId), BizBaseErrorEnum.PARAM_ERROR);
        return redPacketConfigRpc.prepareRedPacket(userId);
    }

    @Override
    public Boolean startRedPacket(Long userId, String code) {
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setRedPacketConfigCode(code);
        LivingRoomRespDTO respDTO = livingRoomRpc.queryByAnchorId(userId);
        ErrorAssert.isNotNull(respDTO, BizBaseErrorEnum.PARAM_ERROR);
        reqDTO.setRoomId(respDTO.getId());
        return redPacketConfigRpc.startRedPacket(reqDTO);
    }

    @Override
    public RedPacketReceiveVO getRedPacket(Long userId, String code) {
        RedPacketConfigReqDTO reqDTO = new RedPacketConfigReqDTO();
        reqDTO.setUserId(userId);
        reqDTO.setRedPacketConfigCode(code);
        RedPacketReceiveDTO receiveDTO = redPacketConfigRpc.receiveRedPacket(reqDTO);
        RedPacketReceiveVO respVO = new RedPacketReceiveVO();
        if(receiveDTO == null) {
            respVO.setMsg("红包已派发完毕");
        } else {
            respVO.setPrice(receiveDTO.getPrice());
            respVO.setMsg(receiveDTO.getNotifyMsg());
        }
        return respVO;
    }
}
