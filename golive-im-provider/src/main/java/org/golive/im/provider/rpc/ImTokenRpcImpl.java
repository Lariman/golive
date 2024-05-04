package org.golive.im.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.im.interfaces.ImTokenRpc;
import org.golive.im.provider.service.ImTokenService;

/**
 * 用户登录token rpc
 */
@DubboService
public class ImTokenRpcImpl implements ImTokenRpc {
    @Resource
    private ImTokenService imTokenService;

    @Override
    public String createImLoginToken(long userId, int appId) {
        return imTokenService.createImLoginToken(userId, appId);
    }

    @Override
    public Long getUserIdByToken(String token) {
        return imTokenService.getUserIdByToken(token);
    }
}
