package org.golive.im.core.server.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.golive.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.golive.framework.redis.starter.key.ImCoreServerProviderCacheKeyBuilder;
import org.golive.im.core.server.service.IMsgAckCheckService;
import org.golive.im.dto.ImMsgBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MsgAckCheckServiceImpl implements IMsgAckCheckService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MsgAckCheckServiceImpl.class);
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImCoreServerProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private MQProducer mqProducer;

    @Override
    public void doMsgAck(ImMsgBody imMsgBody) {
        redisTemplate.opsForHash().delete(cacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(), imMsgBody.getAppId()), imMsgBody.getMsgId());
    }

    @Override
    public void recordMsgAck(ImMsgBody imMsgBody, int times) {
        redisTemplate.opsForHash().put(cacheKeyBuilder.buildImAckMapKey(imMsgBody.getUserId(), imMsgBody.getAppId()), imMsgBody.getMsgId(), times);
    }

    @Override
    public void sendDelayMsg(ImMsgBody imMsgBody) {
        String json = JSON.toJSONString(imMsgBody);
        Message message = new Message();
        message.setBody(json.getBytes());
        message.setTopic(ImCoreServerProviderTopicNames.GOLIVE_IM_ACK_MSG_TOPIC);
        // 等级为1 -> 1s, 等级2 -> 5s
        message.setDelayTimeLevel(2);
        try {
            SendResult sendResult = mqProducer.send(message);
            LOGGER.info("[MsgAckCheckServiceImpl] msg is {}, sendResult is {}", json, sendResult);
        } catch (Exception e) {
            LOGGER.error("[MsgAckCheckServiceImpl] error is {}", e);
        }
    }

    @Override
    public int getMsgAckTimes(String msgId, long userId, int appId) {
        Object value = redisTemplate.opsForHash().get(cacheKeyBuilder.buildImAckMapKey(userId, appId), msgId);
        if(value == null){
            return -1;
        }
        return (int) value;
    }
}
