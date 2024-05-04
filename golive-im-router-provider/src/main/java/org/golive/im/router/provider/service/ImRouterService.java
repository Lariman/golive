package org.golive.im.router.provider.service;

import org.golive.im.dto.ImMsgBody;

import java.util.List;

public interface ImRouterService {

    /**
     * 发送消息
     * @param imMsgBody
     */
    boolean sendMsg(ImMsgBody imMsgBody);

    /**
     * 批量发送消息,群聊场景
     */
    void batchSendMsg(List<ImMsgBody> imMsgBody);
}
