package org.golive.living.provider.rpc;

import jakarta.annotation.Resource;
import org.golive.common.interfaces.dto.PageWrapper;
import org.golive.living.interfaces.dto.LivingPkRespDTO;
import org.golive.living.interfaces.dto.LivingRoomReqDTO;
import org.golive.living.interfaces.dto.LivingRoomRespDTO;
import org.golive.living.interfaces.rpc.ILivingRoomRpc;
import org.golive.living.provider.dao.mapper.LivingRoomMapper;
import org.golive.living.provider.service.ILivingRoomService;

import java.util.List;

public class LivingRoomRpcImpl implements ILivingRoomRpc {

    @Resource
    private ILivingRoomService livingRoomService;
    @Resource
    private LivingRoomMapper livingRoomMapper;

    @Override
    public List<Long> queryUserIdByRoomId(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.queryUserIdByRoomId(livingRoomReqDTO);
    }

    @Override
    public LivingRoomRespDTO queryByAnchorId(Long anchorId) {
        return livingRoomService.queryByAnchorId(anchorId);
    }

    @Override
    public PageWrapper<LivingRoomRespDTO> list(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.list(livingRoomReqDTO);
    }

    @Override
    public LivingRoomRespDTO queryByRoomId(Integer roomId) {
        return livingRoomService.queryByRoomId(roomId);
    }

    @Override
    public Integer startLivingRoom(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.startLivingRoom(livingRoomReqDTO);
    }

    @Override
    public boolean closeLiving(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.closeLiving(livingRoomReqDTO);
    }

    @Override
    public LivingPkRespDTO onlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.onlinePk(livingRoomReqDTO);
    }

    @Override
    public boolean offlinePk(LivingRoomReqDTO livingRoomReqDTO) {
        return livingRoomService.offlinePk(livingRoomReqDTO);
    }

    @Override
    public Long queryOnlinePkUserId(Integer roomId) {
        return livingRoomService.queryOnlinePkUserId(roomId);
    }
}
