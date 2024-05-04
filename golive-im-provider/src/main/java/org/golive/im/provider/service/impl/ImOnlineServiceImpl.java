package org.golive.im.provider.service.impl;

import jakarta.annotation.Resource;
import org.golive.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.golive.im.provider.service.ImOnlineService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class ImOnlineServiceImpl implements ImOnlineService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public boolean isOnline(long userId, int appId) {
        return redisTemplate.hasKey(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);
    }
}
