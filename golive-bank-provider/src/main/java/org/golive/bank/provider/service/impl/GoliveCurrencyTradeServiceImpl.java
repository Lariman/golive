package org.golive.bank.provider.service.impl;

import jakarta.annotation.Resource;
import org.golive.bank.provider.dao.mapper.IGoliveCurrencyTradeMapper;
import org.golive.bank.provider.dao.po.GoliveCurrencyTradePO;
import org.golive.bank.provider.service.IGoliveCurrencyTradeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class GoliveCurrencyTradeServiceImpl implements IGoliveCurrencyTradeService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GoliveCurrencyTradeServiceImpl.class);

    @Resource
    private IGoliveCurrencyTradeMapper goliveCurrencyTradeMapper;

    @Override
    public boolean insertOne(long userId, int num, int type) {
        try{
            GoliveCurrencyTradePO tradePO = new GoliveCurrencyTradePO();
            tradePO.setUserId(userId);
            tradePO.setNum(num);
            tradePO.setType(type);
            goliveCurrencyTradeMapper.insert(tradePO);
            return true;
        }catch(Exception e){
            LOGGER.error("[GoliveCurrencyTradeServiceImpl] insert error is:", e);
        }
        return false;
    }
}
