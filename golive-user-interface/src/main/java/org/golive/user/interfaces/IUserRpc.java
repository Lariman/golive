package org.golive.user.interfaces;

import org.golive.user.dto.UserDTO;

public interface IUserRpc {

    /*
     * 根据用户id进行查询
     * */
    UserDTO getByUserId(Long userId);
}
