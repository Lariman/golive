package org.golive.im.router.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.im.dto.ImMsgBody;
import org.golive.im.router.interfaces.rpc.ImRouterRpc;
import org.golive.im.router.provider.service.ImRouterService;

import java.util.List;

@DubboService
public class ImRouterRpcImpl implements ImRouterRpc {

    @Resource
    private ImRouterService imRouterService;

    @Override
    public boolean sendMsg(ImMsgBody imMsgBody) {
        return imRouterService.sendMsg(imMsgBody);
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBody) {
        imRouterService.batchSendMsg(imMsgBody);
    }
}
