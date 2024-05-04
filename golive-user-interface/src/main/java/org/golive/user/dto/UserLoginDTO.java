package org.golive.user.dto;

import java.io.Serial;
import java.io.Serializable;

public class UserLoginDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4290788036479984698L;

    private boolean isLoginSuccess;  // 是否登录成功
    private String desc;  // 登陆失败返回描述
    private Long userId;

    public boolean isLoginSuccess() {
        return isLoginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        isLoginSuccess = loginSuccess;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public static UserLoginDTO loginError(String desc){
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setLoginSuccess(false);
        userLoginDTO.setDesc(desc);
        return userLoginDTO;
    }

    /**
     * 注册+登录
     * @param userId
     * @return
     */
    public static UserLoginDTO loginSuccess(Long userId){
        UserLoginDTO userLoginDTO = new UserLoginDTO();
        userLoginDTO.setLoginSuccess(true);
        userLoginDTO.setUserId(userId);
        return userLoginDTO;
    }
}
