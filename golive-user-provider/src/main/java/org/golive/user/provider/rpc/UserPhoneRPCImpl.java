package org.golive.user.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.user.dto.UserLoginDTO;
import org.golive.user.dto.UserPhoneDTO;
import org.golive.user.interfaces.IUserPhoneRpc;
import org.golive.user.provider.service.IUserPhoneService;

import java.util.List;

@DubboService
public class UserPhoneRPCImpl implements IUserPhoneRpc {

    @Resource
    private IUserPhoneService userPhoneService;

    @Override
    public UserLoginDTO login(String phone) {
        return userPhoneService.login(phone);
    }

    @Override
    public UserPhoneDTO queryByPhone(String phone) {
        return userPhoneService.queryByPhone(phone);
    }

    @Override
    public List<UserPhoneDTO> queryByUserId(Long userId) {
        return userPhoneService.queryByUserId(userId);
    }
}
