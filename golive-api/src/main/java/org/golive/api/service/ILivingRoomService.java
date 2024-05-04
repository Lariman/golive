package org.golive.api.service;

import org.golive.api.vo.LivingRoomInitVO;
import org.golive.api.vo.req.LivingRoomReqVO;
import org.golive.api.vo.req.OnlinePkReqVO;
import org.golive.api.vo.resp.LivingRoomPageRespVO;
import org.golive.api.vo.resp.LivingRoomRespVO;
import org.golive.api.vo.resp.RedPacketReceiveVO;

public interface ILivingRoomService {

    /**
     * 直播间列表展示
     * @param livingRoomReqVO
     * @return
     */
    LivingRoomPageRespVO list(LivingRoomReqVO livingRoomReqVO);
    /**
     * 开启直播间
     *
     * @param type
     */
    Integer startingLiving(Integer type);

    /**
     * 用户在pk直播间中,连上线请求
     * @param onlinePkReqVO
     * @return
     */
    boolean onlinePk(OnlinePkReqVO onlinePkReqVO);

    /**
     * 关播
     */
    boolean closeLiving(Integer roomId);

    /**
     * 根据用户id返回当前直播间相关信息
     * @param userId
     * @param roomId
     * @return
     */
    LivingRoomInitVO anchorConfig(Long userId, Integer roomId);

    /**
     * 初始化红包数据
     * @param userId
     * @return
     */
    Boolean prepareRedPacket(Long userId, Integer roomId);

    /**
     * 开始红包雨
     * @param userId
     * @param code
     * @return
     */
    Boolean startRedPacket(Long userId, String code);


    /**
     * 领取红包
     * @param userId
     * @param code
     * @return
     */
    RedPacketReceiveVO getRedPacket(Long userId, String code);
}


