package org.golive.im.provider.service;

/**
 * 用户登录token service
 */
public interface ImTokenService {

    /**
     * 生成im服务登录的token
     */
    String createImLoginToken(long userId, int appId);

    /**
     * 根据token获取绑定的userId
     */
    Long getUserIdByToken(String token);
}
