package org.golive.bank.interfaces;

import org.golive.bank.dto.AccountTradeReqDTO;
import org.golive.bank.dto.AccountTradeRespDTO;
import org.golive.bank.dto.GoliveCurrencyAccountDTO;

public interface IGoliveCurrencyAccountRpc {

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
    void decr(long userId, int num);

    /**
     * 扣减虚拟币V2
     * @param userId
     * @param num
     */
    boolean decrV2(long userId, int num);

    /**
     * 查询余额
     * @param userId
     * @return
     */
    Integer getBalance(long userId);

    /**
     * 专门给送礼业务调用的扣减库存逻辑
     */
    AccountTradeRespDTO consumeForSendGift(AccountTradeReqDTO accountTradeReqDTO);

}
