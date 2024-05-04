package org.golive.living.provider.service;

import org.golive.common.interfaces.dto.PageWrapper;
import org.golive.im.core.server.interfaces.dto.ImOfflineDTO;
import org.golive.im.core.server.interfaces.dto.ImOnlineDTO;
import org.golive.living.interfaces.dto.LivingPkRespDTO;
import org.golive.living.interfaces.dto.LivingRoomReqDTO;
import org.golive.living.interfaces.dto.LivingRoomRespDTO;

import java.util.List;

public interface ILivingRoomService {


    /**
     * 支持根据roomId查询出批量的userId(set)存储
     *
     */
    List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO);


    /**
     * 根据主播id查询直播间信息
     * @param anchorId
     * @return
     */
    LivingRoomRespDTO queryByAnchorId(Long anchorId);

    /**
     * 用户下线处理
     * @param imOfflineDTO
     */
    void userOfflineHandler(ImOfflineDTO imOfflineDTO);

    /**
     * 用户上线处理
     * @param imOnlineDTO
     */
    void userOnlineHandler(ImOnlineDTO imOnlineDTO);

    /**
     * 直播间列表的分页查询
     * @param livingRoomReqDTO
     * @return
     */
    PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO);

    List<LivingRoomRespDTO> listAllLivingRoomFromDB(Integer type);

    /**
     * 根据roomId查询直播间
     * @param roomId
     * @return
     */
    LivingRoomRespDTO queryByRoomId(Integer roomId);

    /**
     * 开启直播间
     *
     * @param livingRoomReqDTO
     * @return
     */
    Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 关播
     */
    boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 用户在pk直播间中,连上线请求
     * @param livingRoomReqDTO
     * @return
     */
    LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 用户在pk直播间下线
     * @param livingRoomReqDTO
     * @return
     */
    boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 根据roomId查询当前pk人是谁
     * @param roomId
     * @return
     */
    Long queryOnlinePkUserId(Integer roomId);
}

