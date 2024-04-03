package org.golive.user.provider.service.impl;

import jakarta.annotation.Resource;
import org.golive.common.interfaces.ConvertBeanUtils;
import org.golive.user.dto.UserDTO;
import org.golive.user.provider.dao.mapper.IUserMapper;
import org.golive.user.provider.service.IUserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserMapper userMapper;

    @Override
    public UserDTO getByUserId(Long userId) {
        return ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
    }
}
