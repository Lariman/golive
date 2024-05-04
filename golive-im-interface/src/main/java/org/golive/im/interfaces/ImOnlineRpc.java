package org.golive.im.interfaces;

/**
 * 判断用户是否在线rpc
 */
public interface ImOnlineRpc {

    /**
     * 判断用户是否在线
     * @param userId
     * @param appId
     * @return
     */
    boolean isOnline(long userId, int appId);
}
