package org.golive.gift.interfaces;

import org.golive.gift.dto.GiftRecordDTO;

public interface IGiftRecordRpc {

    /**
     * 插入单个礼物信息
     * @param giftRecordDTO
     */
    void insertOne(GiftRecordDTO giftRecordDTO);
}
