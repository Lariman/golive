package org.golive.im.core.server.service;

import org.golive.im.dto.ImMsgBody;

public interface IRouterHandlerService {

    /**
     * 当收到业务服务的请求后,进行处理
     * @param imMsgBody
     */
    void onReceive(ImMsgBody imMsgBody);

    /**
     * 发送消息
     * @param imMsgBody
     */
    boolean sendMsgToClient(ImMsgBody imMsgBody);
}
