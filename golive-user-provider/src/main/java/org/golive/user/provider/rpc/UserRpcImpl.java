package org.golive.user.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.user.dto.UserDTO;
import org.golive.user.interfaces.IUserRpc;
import org.golive.user.provider.service.IUserService;

@DubboService
public class UserRpcImpl implements IUserRpc {

    @Resource
    private IUserService userService;

    @Override
    public UserDTO getByUserId(Long userId) {
        return userService.getByUserId(userId);
    }
}
