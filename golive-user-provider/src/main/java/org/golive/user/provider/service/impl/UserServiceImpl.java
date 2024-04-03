package org.golive.user.provider.service.impl;

import jakarta.annotation.Resource;
import org.golive.common.interfaces.ConvertBeanUtils;
import org.golive.user.dto.UserDTO;
import org.golive.user.provider.dao.mapper.IUserMapper;
import org.golive.user.provider.dao.po.UserPO;
import org.golive.user.provider.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserMapper userMapper;

    /*
     * 根据用户id进行查询
     * */
    @Override
    public UserDTO getByUserId(Long userId) {
        return ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
    }

    /*
     * 更新用户信息
     * */
    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if(userDTO == null || userDTO.getUserId() == null) return false;
        userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    /*
     * 插入用户信息
     * */
    @Override
    public boolean insertOne(UserDTO userDTO) {
        if(userDTO == null || userDTO.getUserId() == null) return false;
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }
}
