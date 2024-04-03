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

    /*
     * 根据用户id进行查询
     * */
    @Override
    public UserDTO getByUserId(Long userId) {
        return userService.getByUserId(userId);
    }

    /*
     * 更新用户信息
     * */
    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        return userService.updateUserInfo(userDTO);
    }

    /*
     * 插入用户信息
     * */
    @Override
    public boolean insertOne(UserDTO userDTO) {
        return userService.insertOne(userDTO);
    }


}
