package org.golive.im.provider.service;

public interface ImOnlineService {

    /**
     * 判断用户是否在线
     * @param userId
     * @param appId
     * @return
     */
    boolean isOnline(long userId, int appId);
}
