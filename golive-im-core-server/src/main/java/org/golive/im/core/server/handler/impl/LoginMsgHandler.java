package org.golive.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.utils.StringUtils;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.golive.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.golive.im.constants.AppIdEnum;
import org.golive.im.constants.ImConstants;
import org.golive.im.constants.ImMsgCodeEnum;
import org.golive.im.core.server.common.ChannelHandlerContextCache;
import org.golive.im.core.server.common.ImContextUtils;
import org.golive.im.core.server.common.ImMsg;
import org.golive.im.core.server.handler.SimplyHandler;
import org.golive.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.golive.im.core.server.interfaces.dto.ImOnlineDTO;
import org.golive.im.dto.ImMsgBody;
import org.golive.im.interfaces.ImTokenRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 登陆消息的处理逻辑统一收拢到这个类中
 */
@Component
public class LoginMsgHandler implements SimplyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginMsgHandler.class);

    @DubboReference
    private ImTokenRpc imTokenRpc;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MQProducer mqProducer;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        // 防止重复请求
        if(ImContextUtils.getUserId(ctx) != null){
            return;
        }
        // 获取消息体
        byte[] body = imMsg.getBody();
        // 判断消息体是否合法
        if(body == null || body.length == 0){
            ctx.close();
            LOGGER.error("body error, imMsg is {}",  imMsg);
            throw new IllegalArgumentException("body error");
        }
        // 封装消息体:ImMsgBody:有appId/userId/token/data字段
        ImMsgBody imMsgBody = JSON.parseObject(new String(body), ImMsgBody.class);
        Long userIdFromMsg = imMsgBody.getUserId();
        Integer appId = imMsgBody.getAppId();
        String token = imMsgBody.getToken();
        if(StringUtils.isEmpty(token) || userIdFromMsg < 10000 || appId < 10000){
            ctx.close();
            LOGGER.error("param error, imMsg is {}",  imMsg);
            throw new IllegalArgumentException("param error");
        }
        // 从缓存中根据token查
        Long userId = imTokenRpc.getUserIdByToken(token);
        // token校验成功,而且和传递过来的userId是同一个,则允许建立连接
        if(userId != null && userId.equals(userIdFromMsg)){
            loginSuccessHandler(ctx, userId, appId, null);
            return;
        }
        ctx.close();
        LOGGER.error("token check error, imMsg is {}", imMsg);
        throw new IllegalArgumentException("token check error");
    }

    /**
     * 用户登录的时候发送mq消息
     *
     * @param userId
     * @param appId
     */
    private void sendLoginMQ(Long userId, Integer appId, Integer roomId) {
        ImOnlineDTO imOnlineDTO = new ImOnlineDTO();
        imOnlineDTO.setUserId(userId);
        imOnlineDTO.setAppId(appId);
        imOnlineDTO.setRoomId(roomId);
        imOnlineDTO.setLoginTime(System.currentTimeMillis());
        Message message = new Message();
        message.setTopic(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC);
        message.setBody(JSON.toJSONString(imOnlineDTO).getBytes());
        try {
            SendResult sendResult = mqProducer.send(message);
            LOGGER.info("[sendLoginMQ] sendResult is {}", sendResult);
        } catch (Exception e) {
            LOGGER.error("[sendLoginMQ] error is: ", e);
        }
    }



    /**
     * 如果用户登录成功则处理相关记录
     *
     * @param ctx
     * @param userId
     * @param appId
     */
    public void loginSuccessHandler(ChannelHandlerContext ctx, Long userId, Integer appId, Integer roomId) {
        //按照userId保存好相关的channel对象信息
        ChannelHandlerContextCache.put(userId, ctx);
        ImContextUtils.setUserId(ctx, userId);
        ImContextUtils.setAppId(ctx, appId);
        if (roomId != null) {
            ImContextUtils.setRoomId(ctx, roomId);
        }
        //将im消息回写给客户端
        ImMsgBody respBody = new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
        stringRedisTemplate.opsForValue().set(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId,
                ChannelHandlerContextCache.getServerIpAddress() + "%" + userId,
                ImConstants.DEFAULT_HEART_BEAT_GAP * 2, TimeUnit.SECONDS);
        LOGGER.info("[LoginMsgHandler] login success,userId is {},appId is {}", userId, appId);
        ctx.writeAndFlush(respMsg);
        sendLoginMQ(userId, appId, roomId);
    }
}
