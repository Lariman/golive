package org.golive.bank.provider.service;

import org.golive.bank.dto.AccountTradeReqDTO;
import org.golive.bank.dto.AccountTradeRespDTO;
import org.golive.bank.dto.GoliveCurrencyAccountDTO;

public interface IGoliveCurrencyAccountService {

    /**
     * 新增账户
     * @param userId
     */
    boolean insertOne(long userId);

    /**
     * 增加虚拟币
     * @param userId
     * @param num
     */
    void incr(long userId, int num);

    /**
     * 扣减虚拟币
     * @param userId
     * @param num
     */
    boolean decr(long userId, int num);

    /**
     * 查询余额
     * @param userId
     * @return
     */
    Integer getBalance(long userId);

    /**
     * 专门给送礼业务调用的扣减余额逻辑
     */
    AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO);
}
