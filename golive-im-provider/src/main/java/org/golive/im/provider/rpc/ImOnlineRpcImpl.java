package org.golive.im.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.im.interfaces.ImOnlineRpc;
import org.golive.im.provider.service.ImOnlineService;

@DubboService
public class ImOnlineRpcImpl implements ImOnlineRpc {

    @Resource
    private ImOnlineService imOnlineService;

    @Override
    public boolean isOnline(long userId, int appId) {
        return imOnlineService.isOnline(userId, appId);
    }
}
