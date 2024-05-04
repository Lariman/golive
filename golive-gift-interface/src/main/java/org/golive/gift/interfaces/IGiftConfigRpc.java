package org.golive.gift.interfaces;

import org.golive.gift.dto.GiftConfigDTO;

import java.util.List;

public interface IGiftConfigRpc {

    /**
     * 按照礼物id查询
     * @param giftId
     * @return
     */
    GiftConfigDTO getByGiftId(Integer giftId);

    /**
     * 查询所有礼物信息
     * @return
     */
    List<GiftConfigDTO> queryGiftList();

    /**
     * 插入单个礼物信息
     * @param giftConfigDTO
     */
    void insertOne(GiftConfigDTO giftConfigDTO);

    /**
     * 更新单个礼物信息
     * @param giftConfigDTO
     */
    void updateOne(GiftConfigDTO giftConfigDTO);
}
