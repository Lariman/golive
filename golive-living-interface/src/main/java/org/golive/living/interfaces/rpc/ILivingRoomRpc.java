package org.golive.living.interfaces.rpc;

import org.golive.common.interfaces.dto.PageWrapper;
import org.golive.living.interfaces.dto.LivingPkRespDTO;
import org.golive.living.interfaces.dto.LivingRoomReqDTO;
import org.golive.living.interfaces.dto.LivingRoomRespDTO;

import java.util.List;

public interface ILivingRoomRpc {

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
     * 直播间列表的分页查询
     * @param livingRoomReqDTO
     * @return
     */
    PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO);

    /**
     * 根据用户id查询是否正在开播
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
