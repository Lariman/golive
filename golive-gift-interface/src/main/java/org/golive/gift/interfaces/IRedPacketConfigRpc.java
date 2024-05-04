package org.golive.gift.interfaces;

import org.golive.gift.dto.RedPacketConfigReqDTO;
import org.golive.gift.dto.RedPacketConfigRespDTO;
import org.golive.gift.dto.RedPacketReceiveDTO;


public interface IRedPacketConfigRpc {

    /**
     * 按照主播id查询红包雨配置
     * @param anchorId
     * @return
     */
    RedPacketConfigRespDTO queryByAnchorId(Long anchorId);

    /**
     * 新增红包配置
     * @param redPacketConfigReqDTO
     * @return
     */
    boolean addOne(RedPacketConfigReqDTO redPacketConfigReqDTO);

    /**
     * 更新红包雨配置
     * @param redPacketConfigReqDTO
     * @return
     */
    boolean updateById(RedPacketConfigReqDTO redPacketConfigReqDTO);

    /**
     * 提前生成红包雨的数据
     * @param anchorId
     * @return
     */
    boolean prepareRedPacket(Long anchorId);

    /**
     * 领取红包
     * @param reqDTO
     * @return
     */
    RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO);

    /**
     * 广播直播间用户,开始抢红包
     * @param reqDTO
     * @return
     */
    Boolean startRedPacket(RedPacketConfigReqDTO reqDTO);
}
