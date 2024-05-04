package org.golive.gift.provider.consumer;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.common.consumer.ConsumeFromWhere;
import org.golive.common.interfaces.topic.GiftProviderTopicNames;
import org.golive.framework.mq.starter.properties.RocketMQConsumerProperties;
import org.golive.gift.dto.RollBackStockDTO;
import org.golive.gift.provider.service.ISkuStockInfoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

public class StockRollBackConsumer implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(StockRollBackConsumer.class);

    @Resource
    private RocketMQConsumerProperties rocketMQConsumerProperties;
    @Resource
    private ISkuStockInfoService skuStockInfoService;

    @Override
    public void afterPropertiesSet() throws Exception {
        DefaultMQPushConsumer mqPushConsumer = new DefaultMQPushConsumer();
        mqPushConsumer.setVipChannelEnabled(false);
        mqPushConsumer.setNamesrvAddr(rocketMQConsumerProperties.getNameSrv());
        mqPushConsumer.setConsumerGroup(rocketMQConsumerProperties.getGroupName() + "_" + StockRollBackConsumer.class.getSimpleName());
        mqPushConsumer.setConsumeMessageBatchMaxSize(1);
        mqPushConsumer.setConsumeFromWhere(ConsumeFromWhere.CONSUME_FROM_FIRST_OFFSET);

        mqPushConsumer.subscribe(GiftProviderTopicNames.ROLL_BACK_STOCK, "");
        mqPushConsumer.setMessageListener((MessageListenerConcurrently) (msgs, context) -> {
            try{
                String msgStr = String.valueOf(msgs.get(0));
                skuStockInfoService.stockRollBackHandler(JSON.parseObject(msgStr, RollBackStockDTO.class));
            } catch (Exception e) {
                return ConsumeConcurrentlyStatus.RECONSUME_LATER;
            }
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        mqPushConsumer.start();
        LOGGER.info("[StockRollBackConsumer] consumer has started!");

    }
}
