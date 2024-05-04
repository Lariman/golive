package org.golive.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.gift.dto.RedPacketConfigReqDTO;
import org.golive.gift.dto.RedPacketConfigRespDTO;
import org.golive.gift.dto.RedPacketReceiveDTO;
import org.golive.gift.interfaces.IRedPacketConfigRpc;
import org.golive.gift.provider.dao.po.RedPacketConfigPO;
import org.golive.gift.provider.service.IRedPacketConfigService;

@DubboService
public class RedPacketConfigRpcImpl implements IRedPacketConfigRpc {

    @Resource
    private IRedPacketConfigService redPacketConfigService;

    @Override
    public RedPacketConfigRespDTO queryByAnchorId(Long anchorId) {
        return ConvertBeanUtils.convert(redPacketConfigService.queryByAnchorId(anchorId), RedPacketConfigRespDTO.class);
    }

    @Override
    public boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.addOne(ConvertBeanUtils.convert(redPacketConfigReqDTO, RedPacketConfigPO.class));
    }

    @Override
    public boolean updateById(RedPacketConfigReqDTO redPacketConfigReqDTO) {
        return redPacketConfigService.updateById(ConvertBeanUtils.convert(redPacketConfigReqDTO, RedPacketConfigPO.class));
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        return redPacketConfigService.prepareRedPacket(anchorId);
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.receiveRedPacket(reqDTO);
    }

    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        return redPacketConfigService.startRedPacket(reqDTO);
    }
}
