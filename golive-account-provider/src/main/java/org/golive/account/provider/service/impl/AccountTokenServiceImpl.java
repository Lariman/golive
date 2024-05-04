package org.golive.account.provider.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import jakarta.annotation.Resource;
import org.golive.account.provider.service.IAccountTokenService;
import org.golive.framework.redis.starter.key.AccountProviderCacheKeyBuilder;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class AccountTokenServiceImpl implements IAccountTokenService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private AccountProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public String createAndSaveLoginToken(Long userId) {
        String token = UUID.randomUUID().toString();
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildUserLoginToken(token), String.valueOf(userId), 30, TimeUnit.DAYS);
        return token;
    }

    @Override
    public Long getUserIdByToken(String tokenKey) {
        String redisKey = cacheKeyBuilder.buildUserLoginToken(tokenKey);
        Integer userId = (Integer) redisTemplate.opsForValue().get(redisKey);
        if(userId == null){
            return null;
        }
        return Long.valueOf(userId);
    }
}
