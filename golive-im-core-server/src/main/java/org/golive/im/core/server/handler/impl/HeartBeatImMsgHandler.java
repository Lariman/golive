package org.golive.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.golive.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.golive.im.constants.ImConstants;
import org.golive.im.constants.ImMsgCodeEnum;
import org.golive.im.core.server.common.ImContextUtils;
import org.golive.im.core.server.common.ImMsg;
import org.golive.im.core.server.handler.SimplyHandler;
import org.golive.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.golive.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 心跳消息处理器
 */
@Component
public class HeartBeatImMsgHandler implements SimplyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginMsgHandler.class);

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        // 心跳包基本校验
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId == null || appId == null){
            LOGGER.error("attr error, imMsg is {}",  imMsg);
            ctx.close();
            throw new IllegalArgumentException("attr is error");
        }
        // 心跳包record记录,redis存储心跳记录
        String redisKey = cacheKeyBuilder.buildImLoginTokenKey(userId, appId);
        // golive-im-core-server:heartbeat:999:zset
        // zSet集合存储心跳记录,基于userId做取模, key(userId)-score(心跳时间戳)
        this.recordOnlineTime(userId, redisKey);
        this.removeExpireRecord(redisKey);
        redisTemplate.expire(redisKey, 5, TimeUnit.MINUTES);
        // 延长用户之前保存的ip绑定记录事件
        stringRedisTemplate.expire(ImCoreServerConstants.IM_BIND_IP_KEY + appId + userId, ImConstants.DEFAULT_HEART_BEAT_GAP * 2, TimeUnit.SECONDS);
        ImMsgBody msgBody = new ImMsgBody();
        msgBody.setUserId(userId);
        msgBody.setAppId(appId);
        msgBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_HEARTBEAT_MSG.getCode(), JSON.toJSONString(msgBody));
        LOGGER.info("[HeartBeatImMsgHandler] imMsg is {}", imMsg);
        ctx.writeAndFlush(respMsg);
    }

    /**
     * 清理掉过期不在线的用户留下的心跳记录(在两次心跳包的发送间隔中,如果没有重新更新score值,就会导致被删除)
     * @param redisKey
     */
    private void removeExpireRecord(String redisKey) {
        redisTemplate.opsForZSet().removeRangeByScore(redisKey, 0, System.currentTimeMillis() - ImConstants.DEFAULT_HEART_BEAT_GAP * 1000 * 2);
    }

    /**
     * 记录用户最近一次心跳事件到zSet上
     * @param userId
     * @param redisKey
     */
    private void recordOnlineTime(Long userId, String redisKey) {
        redisTemplate.opsForZSet().add(redisKey, userId, System.currentTimeMillis());
    }
}
