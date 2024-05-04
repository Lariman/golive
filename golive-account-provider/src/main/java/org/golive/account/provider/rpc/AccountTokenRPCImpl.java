package org.golive.account.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.account.interfaces.IAccountTokenRPC;
import org.golive.account.provider.service.IAccountTokenService;

@DubboService
public class AccountTokenRPCImpl implements IAccountTokenRPC {
    @Resource
    private IAccountTokenService accountTokenService;

    @Override
    public String createAndSaveLoginToken(Long userId) {
        return accountTokenService.createAndSaveLoginToken(userId);
    }

    @Override
    public Long getUserIdByToken(String tokenKey) {
        return accountTokenService.getUserIdByToken(tokenKey);
    }
}
