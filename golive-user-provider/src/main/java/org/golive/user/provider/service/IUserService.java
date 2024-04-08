package org.golive.user.provider.service;

import org.golive.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

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

    /*
    * 批量查询用户id
    * */
    Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList);
}
