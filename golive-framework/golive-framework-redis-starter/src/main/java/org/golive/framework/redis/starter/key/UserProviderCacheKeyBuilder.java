package org.golive.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class UserProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String USER_INFO_KEY = "userInfo";
    private static String USER_TAG_LOCK_KEY = "userTagLock";
    private static String USER_TAG_KEY = "userTag";
    private static String USER_PHONE_LIST_KEY = "userPhoneList";
    private static String USER_PHONE_OBJ_KEY = "userPhoneObj";
    private static String USER_LOGIN_TOKEN_KEY = "userLoginToken";


    public String buildUserInfoKey(Long userId) {
        // 前缀(应用名) + 用户Key + : + userId
        return super.getPrefix() + USER_INFO_KEY + super.getSplitItem() + userId;
    }

    public String buildTagLockKey(Long userId){
        return super.getPrefix() + USER_TAG_LOCK_KEY + super.getSplitItem() + userId;
    }

    public String buildTagKey(Long userId){
        return super.getPrefix() + USER_TAG_KEY + super.getSplitItem() + userId;
    }

    public String buildUserPhoneListKey(Long userId){
        return super.getPrefix() + USER_PHONE_LIST_KEY + super.getSplitItem() + userId;
    }

    public String buildUserPhoneObjKey(String phone){
        return super.getPrefix() + USER_PHONE_OBJ_KEY + super.getSplitItem() + phone;
    }

    public String buildUserLoginTokenKey(String tokenKey){
        return super.getPrefix() + USER_LOGIN_TOKEN_KEY + super.getSplitItem() + tokenKey;
    }
}