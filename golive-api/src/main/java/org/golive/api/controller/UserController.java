package org.golive.api.controller;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.user.dto.UserDTO;
import org.golive.user.interfaces.IUserRpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user")
public class UserController {

    @DubboReference
    private IUserRpc userRpc;

    /*
    * 获取用户信息
    * */
    @GetMapping("/getUserInfo")
    public UserDTO getUserInfo(Long userId){
        return userRpc.getByUserId(userId);
    }

    @GetMapping("/batchQueryUserInfo")
    public Map<Long, UserDTO> batchQueryUserInfo(String userIdStr){
        return userRpc.batchQueryUserInfo(Arrays.asList(userIdStr.split(",")).stream().map(x -> Long.valueOf(x)).collect(Collectors.toList()));
    }

    /*
    * 更新用户信息
    * */
    @GetMapping("/updateUserInfo")
    public boolean updateUserInfo(Long userId, String nickname){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName(nickname);
        return userRpc.updateUserInfo(userDTO);
    }

    /*
    * 插入用户信息
    * */
    @GetMapping("/insertOne")
    public boolean insertOne(Long userId, String nickname, Integer gender){
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName(nickname);
        userDTO.setSex(gender);
        return userRpc.insertOne(userDTO);
    }
}
