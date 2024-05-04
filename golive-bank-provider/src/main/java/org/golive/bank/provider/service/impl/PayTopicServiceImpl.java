package org.golive.bank.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.golive.bank.provider.dao.mapper.IPayTopicMapper;
import org.golive.bank.provider.dao.po.PayTopicPO;
import org.golive.bank.provider.service.IPayTopicService;
import org.golive.common.interfaces.enums.CommonStatusEum;

public class PayTopicServiceImpl implements IPayTopicService {

    @Resource
    private IPayTopicMapper payTopicMapper;

    @Override
    public PayTopicPO getByCode(Integer code) {
        LambdaQueryWrapper<PayTopicPO> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(PayTopicPO::getBizCode, code);
        lambdaQueryWrapper.eq(PayTopicPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        lambdaQueryWrapper.last("limit 1");
        return payTopicMapper.selectOne(lambdaQueryWrapper);
    }
}
