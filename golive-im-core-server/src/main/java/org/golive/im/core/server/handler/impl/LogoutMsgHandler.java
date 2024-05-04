package org.golive.im.core.server.handler.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.ChannelHandlerContext;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.golive.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.golive.im.constants.ImMsgCodeEnum;
import org.golive.im.core.server.common.ChannelHandlerContextCache;
import org.golive.im.core.server.common.ImContextUtils;
import org.golive.im.core.server.common.ImMsg;
import org.golive.im.core.server.handler.SimplyHandler;
import org.golive.im.core.server.interfaces.constants.ImCoreServerConstants;
import org.golive.im.core.server.interfaces.dto.ImOfflineDTO;
import org.golive.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 登出消息的处理逻辑
 */
@Component
public class LogoutMsgHandler implements SimplyHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginMsgHandler.class);

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private MQProducer mqProducer;

    @Override
    public void handler(ChannelHandlerContext ctx, ImMsg imMsg) {
        Long userId = ImContextUtils.getUserId(ctx);
        Integer appId = ImContextUtils.getAppId(ctx);
        if(userId == null || appId == null){
            LOGGER.error("attr error, imMsg is {}",  imMsg);
            ctx.close();
            throw new IllegalArgumentException("body error");
        }
        // 将im消息回写给客户端
        logoutHandler(ctx, userId, appId);
        sendLogoutMQ(userId, appId);
    }

    private void sendLogoutMQ(ChannelHandlerContext ctx, Long userId, Integer appId){
        ImOfflineDTO imOfflineDTO = new ImOfflineDTO();
        imOfflineDTO.setUserId(userId);
        imOfflineDTO.setRoomId(ImContextUtils.getRoomId(ctx));
        imOfflineDTO.setAppId(appId);
        imOfflineDTO.setLoginTime(System.currentTimeMillis());
        Message message = new Message();
        message.setTopic(ImCoreServerProviderTopicNames.IM_OFFLINE_TOPIC);
        message.setBody(JSONObject.toJSONString(imOfflineDTO).getBytes());
        try {
            SendResult sendResult = mqProducer.send(message);
            LOGGER.info("[sendLoginMQ] sendResult is {}", sendResult);
        } catch (Exception e) {
            LOGGER.error("[sendLogoutMQ] error is: ", e);
        }


    }

    private void logoutHandler(ChannelHandlerContext ctx, Long userId, Integer appId){
        ImMsgBody respBody = new ImMsgBody();
        respBody.setAppId(appId);
        respBody.setUserId(userId);
        respBody.setData("true");
        ImMsg respMsg = ImMsg.build(ImMsgCodeEnum.IM_LOGIN_MSG.getCode(), JSON.toJSONString(respBody));
        // 成功建立连接, 将结果回写给客户端
        LOGGER.info("[LogoutMsgHandler] logout success, userId id {}, appId is {}", userId, appId);
        ctx.writeAndFlush(respMsg);
        // 理想情况下,客户端断线的时候,会发送一个断线消息包
        // ImMsgBody imMsgBody = JSON.parseObject(new String(body), ImMsgBody.class);
        // Long userId = imMsgBody.getUserId();
        ChannelHandlerContextCache.remove(userId);

        stringRedisTemplate.delete(ImCoreServerConstants.IM_BIND_IP_KEY + appId + ":" + userId);

        ImContextUtils.removeUserId(ctx);
        ImContextUtils.removeAppId(ctx);
        ctx.close();
    }
}
