package org.golive.user.provider.service;

import org.golive.user.dto.UserLoginDTO;
import org.golive.user.dto.UserPhoneDTO;

import java.util.List;

public interface IUserPhoneService {

    /**
     * 用户登录(底层会进行手机号的注册)
     * @param phone
     * @return
     */
    UserLoginDTO login(String phone);

    /**
     * 根据手机信息查询相关用户信息
     * @param phone
     * @return
     */
    UserPhoneDTO queryByPhone(String phone);

    /**
     * 根据用户id批量查询手机相关信息
     * @param userId
     * @return
     */
    List<UserPhoneDTO> queryByUserId(Long userId);
}
