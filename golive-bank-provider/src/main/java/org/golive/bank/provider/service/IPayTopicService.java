package org.golive.bank.provider.service;

import org.golive.bank.provider.dao.po.PayTopicPO;

public interface IPayTopicService {

    /**
     * 根据code查询
     * @param code
     * @return
     */
    PayTopicPO getByCode(Integer code);
}
