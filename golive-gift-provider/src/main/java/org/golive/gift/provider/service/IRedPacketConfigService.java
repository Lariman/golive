package org.golive.gift.provider.service;

import org.golive.gift.dto.RedPacketConfigReqDTO;
import org.golive.gift.dto.RedPacketReceiveDTO;
import org.golive.gift.provider.dao.po.RedPacketConfigPO;


public interface IRedPacketConfigService {

    /**
     * 支持根据主播id查询是否有红包雨配置特权
     * @param anchorId
     * @return
     */
    RedPacketConfigPO queryByAnchorId(Long anchorId);

    /**
     * 根据红包雨配置code检索信息
     * @param configCode
     * @return
     */
    RedPacketConfigPO queryByConfigCode(String configCode);

    /**
     * 新增红包配置
     * @param redPacketConfigPO
     * @return
     */
    boolean addOne(RedPacketConfigPO redPacketConfigPO);

    /**
     * 更新红包雨配置
     * @param redPacketConfigPO
     * @return
     */
    boolean updateById(RedPacketConfigPO redPacketConfigPO);

    /**
     * 提前生成红包雨的数据
     * @param anchorId
     * @return
     */
    boolean prepareRedPacket(Long anchorId);

    // 红包怎么领取

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
