package org.golive.gift.provider.service.impl;

import jakarta.annotation.Resource;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.gift.dto.GiftRecordDTO;
import org.golive.gift.provider.dao.mapper.GiftRecordMapper;
import org.golive.gift.provider.dao.po.GiftRecordPO;
import org.golive.gift.provider.service.IGiftRecordService;
import org.springframework.stereotype.Service;

@Service
public class GiftRecordServiceImpl implements IGiftRecordService {

    @Resource
    private GiftRecordMapper giftRecordMapper;

    @Override
    public void insertOne(GiftRecordDTO giftRecordDTO) {
        GiftRecordPO giftRecordPO = ConvertBeanUtils.convert(giftRecordDTO, GiftRecordPO.class);
        giftRecordMapper.insert(giftRecordPO);
    }
}
