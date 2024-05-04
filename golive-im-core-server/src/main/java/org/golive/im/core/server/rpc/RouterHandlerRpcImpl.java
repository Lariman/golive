package org.golive.im.core.server.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.im.core.server.interfaces.rpc.IRouterHandlerRpc;
import org.golive.im.core.server.service.IRouterHandlerService;
import org.golive.im.dto.ImMsgBody;

import java.util.List;

@DubboService
public class RouterHandlerRpcImpl implements IRouterHandlerRpc {

    @Resource
    private IRouterHandlerService routerHandlerService;

    @Override
    public void sendMsg(ImMsgBody imMsgBody) {
        routerHandlerService.onReceive(imMsgBody);
    }

    @Override
    public void batchSendMsg(List<ImMsgBody> imMsgBodyList) {
        imMsgBodyList.forEach(imMsgBody -> {
            routerHandlerService.onReceive(imMsgBody);
        });
    }
}
