package org.golive.im.core.server.interfaces.rpc;

import org.golive.im.dto.ImMsgBody;

import java.util.List;

/**
 * 专门给router层的服务进行调用的接口
 */
public interface IRouterHandlerRpc {

    /**
     * 按照用户id进行消息的发送
     */
    void sendMsg(ImMsgBody imMsgBody);

    /**
     * 批量发送消息
     */
    void batchSendMsg(List<ImMsgBody> imMsgBodyList);
}
