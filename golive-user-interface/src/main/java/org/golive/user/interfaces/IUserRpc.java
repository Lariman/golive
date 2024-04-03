package org.golive.user.interfaces;

import org.golive.user.dto.UserDTO;

public interface IUserRpc {

    /*
     * 根据用户id进行查询
     * */
    UserDTO getByUserId(Long userId);

    /*
    * 更新用户信息
    * */
    boolean updateUserInfo(UserDTO userDTO);

    /*
    * 插入用户信息
    * */
    boolean insertOne(UserDTO userDTO);
}
