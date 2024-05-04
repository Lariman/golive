package org.golive.msg.interfaces;

import org.golive.msg.dto.MsgCheckDTO;
import org.golive.msg.enums.MsgSendResultEnum;

public interface ISmsRpc {
    /**
     * 发送短信接口
     *
     * @param phone
     * @return
     */
    MsgSendResultEnum sendLoginCode(String phone);

    /**
     * 校验登录验证码
     *
     * @param phone
     * @param code
     * @return
     */
    MsgCheckDTO checkLoginCode(String phone, Integer code);

    /**
     * 插入一条短信记录
     *
     * @param phone
     * @param code
     */
    void insertOne(String phone, Integer code);
}

