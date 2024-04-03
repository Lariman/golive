package org.golive.api.controller;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.user.dto.UserDTO;
import org.golive.user.interfaces.IUserRpc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

    @DubboReference
    private IUserRpc userRpc;

    @GetMapping("/getUserInfo")
    public UserDTO getUserInfo(Long userId){
        return userRpc.getByUserId(userId);
    }
}
