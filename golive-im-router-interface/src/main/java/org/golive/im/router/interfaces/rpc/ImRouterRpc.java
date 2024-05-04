package org.golive.im.router.interfaces.rpc;

import org.golive.im.dto.ImMsgBody;

import java.util.List;

/**
 * 专门给router层的服务进行调用的接口
 */
public interface ImRouterRpc {

    /**
     * 发送消息
     */
    boolean sendMsg(ImMsgBody imMsgBody);

    /**
     * 批量发送消息,在直播间内
     */
    void batchSendMsg(List<ImMsgBody> imMsgBody);
}
