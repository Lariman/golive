package org.golive.user.provider.service;

import org.golive.user.dto.UserDTO;

public interface IUserService {

    /*
     * 根据用户id进行查询
     * */
    UserDTO getByUserId(Long UserId);

    /*
     * 更新用户信息
     * */
    boolean updateUserInfo(UserDTO userDTO);

    /*
     * 插入用户信息
     * */
    boolean insertOne(UserDTO userDTO);
}
