package org.golive.api.controller;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletResponse;
import org.golive.api.service.IUserLoginService;
import org.golive.common.interfaces.vo.WebResponseVO;
import org.golive.api.service.IUserLoginService;
import org.golive.common.interfaces.vo.WebResponseVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author idea
 * @Date: Created in 10:43 2023/6/15
 * @Description
 */
@RestController
@RequestMapping("/userLogin")
public class UserLoginController {

    @Resource
    private IUserLoginService userLoginService;

    //发送验证码
    @PostMapping("/sendLoginCode")
    public WebResponseVO sendLoginCode(String phone) {
        return userLoginService.sendLoginCode(phone);
    }

    //登录请求 验证码是否合法 -> 初始化注册/老用户登录
    @PostMapping("/login")
    public WebResponseVO login(String phone, Integer code, HttpServletResponse response) {
        return userLoginService.login(phone, code, response);
    }

}
