package org.golive.bank.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.bank.dto.AccountTradeReqDTO;
import org.golive.bank.dto.AccountTradeRespDTO;
import org.golive.bank.dto.GoliveCurrencyAccountDTO;
import org.golive.bank.interfaces.IGoliveCurrencyAccountRpc;
import org.golive.bank.provider.service.IGoliveCurrencyAccountService;

@DubboService
public class GoliveCurrencyAccountRpcImpl implements IGoliveCurrencyAccountRpc {

    @Resource
    private IGoliveCurrencyAccountService goliveCurrencyAccountService;

    @Override
    public void incr(long userId, int num) {
        goliveCurrencyAccountService.incr(userId, num);
    }

    @Override
    public void decr(long userId, int num) {
        goliveCurrencyAccountService.decr(userId, num);
    }

    @Override
    public boolean decrV2(long userId, int num) {
        Integer price = goliveCurrencyAccountService.getBalance(userId);
        if((price - num) < 0){
           return false;
        }
        return goliveCurrencyAccountService.decr(userId, num);
    }

    @Override
    public Integer getBalance(long userId) {
        return goliveCurrencyAccountService.getBalance(userId);
    }

    @Override
    public AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO) {
        return goliveCurrencyAccountService.consumeForSendGift(accountTradeReqDTO);
    }


}
