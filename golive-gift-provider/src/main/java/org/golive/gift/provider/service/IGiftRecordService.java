package org.golive.gift.provider.service;

import org.golive.gift.dto.GiftRecordDTO;

public interface IGiftRecordService {

    /**
     * 插入单个礼物信息
     * @param giftRecordDTO
     */
    void insertOne(GiftRecordDTO giftRecordDTO);
}
