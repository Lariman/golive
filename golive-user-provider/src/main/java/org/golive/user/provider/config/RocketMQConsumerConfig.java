// package org.golive.user.provider.config;
//
// /*
//  * RocketMQ的消费者bean配置
//  * */
//
// import com.alibaba.fastjson2.JSON;
// import jakarta.annotation.Resource;
// import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
// import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
// import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
// import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
// import org.apache.rocketmq.client.exception.MQClientException;
// import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
// import org.apache.rocketmq.common.message.MessageExt;
// import org.golive.framework.redis.starter.key.UserProviderCacheKeyBuilder;
// import org.golive.user.constants.CacheAsyncDeleteCode;
// import org.golive.user.constants.UserProviderTopicNames;
// import org.golive.user.dto.UserCacheAsyncDeleteDTO;
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
// import org.springframework.beans.factory.InitializingBean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.data.redis.core.RedisTemplate;
//
// import java.util.List;
//
// @Configuration
// public class RocketMQConsumerConfig implements InitializingBean {
//
//     private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQConsumerConfig.class);
//
//     @Resource
//     private RocketMQConsumerProperties consumerProperties;
//
//     @Resource
//     private RedisTemplate<String, Object> redisTemplate;
//
//     @Resource
//     private UserProviderCacheKeyBuilder cacheKeyBuilder;
//
//     // 初始化方式一:
//     @Override
//     public void afterPropertiesSet() throws Exception {
//         initConsumer();
//     }
//
//     // 初始化方式二:@PostConstruct
//     public void initConsumer() {
//         try {
//             // 初始化RockerMQ消费者
//             DefaultMQPushConsumer defaultMQPushConsumer = new DefaultMQPushConsumer();
//             defaultMQPushConsumer.setNamesrvAddr(consumerProperties.getNameSrv());  // 设置服务端地址
//             defaultMQPushConsumer.setConsumerGroup(consumerProperties.getGroupName());  // 设置消费者组
//             defaultMQPushConsumer.setConsumeMessageBatchMaxSize(1);
//             defaultMQPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
//             defaultMQPushConsumer.setVipChannelEnabled(false);
//             defaultMQPushConsumer.subscribe(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC, "");
//             defaultMQPushConsumer.setMessageListener(new MessageListenerConcurrently() {
//                 @Override
//                 public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
//                     // 获取消息
//                     String json = new String(msgs.get(0).getBody());
//                     // 解析消息
//                     UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = JSON.parseObject(json, UserCacheAsyncDeleteDTO.class);
//                     if(CacheAsyncDeleteCode.USER_INFO_DELETE.getCode() == userCacheAsyncDeleteDTO.getCode()){  // 用户信息删除
//                         Long userId = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
//                         redisTemplate.delete(cacheKeyBuilder.buildUserInfoKey(userId));
//                         LOGGER.info("延迟删除用户信息缓存, userId is {}", userId);
//                     }else if(CacheAsyncDeleteCode.USER_TAG_DELETE.getCode() == userCacheAsyncDeleteDTO.getCode()){  // 用户标签删除
//                         Long userId = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
//                         redisTemplate.delete(cacheKeyBuilder.buildTagKey(userId));
//                         LOGGER.info("延迟删除用户标签缓存, userId is {}", userId);
//                     }
//                     return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
//                 }
//             });
//             defaultMQPushConsumer.start();
//             LOGGER.info("mq消费者启动成功,nameSrv is {}", consumerProperties.getNameSrv());
//         } catch (MQClientException e) {
//             throw new RuntimeException(e);
//         }
//
//
//
//     }
// }

package org.golive.user.provider.config;

import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.apache.rocketmq.common.message.MessageExt;
import org.golive.user.provider.config.RocketMQConsumerProperties;
import org.golive.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.golive.common.interfaces.topic.UserProviderTopicNames;
import org.golive.user.constants.CacheAsyncDeleteCode;
import org.golive.user.dto.UserCacheAsyncDeleteDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

@Configuration
public class RocketMQConsumerConfig implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RocketMQConsumerConfig.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private ApplicationContext applicationContext;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + RocketMQConsumerConfig.class.getSimpleName());
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);
        mqPushConsumer.subscribe(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC, "");
        mqPushConsumer.setMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> msgs, ConsumeConcurrentlyContext context) {
                String json = new String(msgs.get(0).getBody());
                UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = JSON.parseObject(json, UserCacheAsyncDeleteDTO.class);
                if (CacheAsyncDeleteCode.USER_INFO_DELETE.getCode() == userCacheAsyncDeleteDTO.getCode()) {
                    Long userId = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                    redisTemplate.delete(cacheKeyBuilder.buildUserInfoKey(userId));
                    LOGGER.info("延迟删除用户信息缓存，userId is {}",userId);
                } else if (CacheAsyncDeleteCode.USER_TAG_DELETE.getCode() == userCacheAsyncDeleteDTO.getCode()) {
                    Long userId = JSON.parseObject(userCacheAsyncDeleteDTO.getJson()).getLong("userId");
                    redisTemplate.delete(cacheKeyBuilder.buildTagKey(userId));
                    LOGGER.info("延迟删除用户标签缓存，userId is {}",userId);
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        mqPushConsumer.start();
        LOGGER.info("mq消费者启动成功,namesrv is {}", rocketMQConsumerProperties.getNameSrv());
    }
}

