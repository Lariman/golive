package org.golive.im.interfaces;

public interface ImTokenRpc {

    /**
     * 生成im服务登录的token
     */
    String createImLoginToken(long userId, int appId);

    /**
     * 根据token获取绑定的userId
     */
    Long getUserIdByToken(String token);
}
