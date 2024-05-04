package org.golive.living.provider.consumer;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.golive.common.interfaces.topic.ImCoreServerProviderTopicNames;
import org.golive.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.golive.im.core.server.interfaces.dto.ImOnlineDTO;
import org.golive.living.provider.service.ILivingRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class LivingRoomOnlineConsumer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(LivingRoomOnlineConsumer.class);
    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private ILivingRoomService livingRoomService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + LivingRoomOnlineConsumer.class.getSimpleName());
        // 一次从broker中拉取10条消息到本地内存中进行消费
        mqPushConsumer.setConsumeMessageBatchMaxSize(10);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        // 监听im发送过来的业务消息topic
        mqPushConsumer.subscribe(ImCoreServerProviderTopicNames.IM_ONLINE_TOPIC, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            for (MessageExt msg : msgs) {
                livingRoomService.userOnlineHandler(JSON.parseObject(new String(msg.getBody()), ImOnlineDTO.class));
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        LOGGER.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }
}
