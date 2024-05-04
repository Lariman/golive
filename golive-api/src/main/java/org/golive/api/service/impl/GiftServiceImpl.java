package org.golive.api.service.impl;

import com.alibaba.fastjson2.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.golive.api.error.ApiErrorEnum;
import org.golive.api.service.IGiftService;
import org.golive.api.vo.req.GiftReqVO;
import org.golive.api.vo.resp.GiftConfigVO;
import org.golive.bank.dto.AccountTradeReqDTO;
import org.golive.bank.dto.AccountTradeRespDTO;
import org.golive.bank.interfaces.IGoliveCurrencyAccountRpc;
import org.golive.common.interfaces.dto.SendGiftMq;
import org.golive.common.interfaces.topic.GiftProviderTopicNames;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.gift.dto.GiftConfigDTO;
import org.golive.gift.interfaces.IGiftConfigRpc;
import org.golive.web.starter.context.GoliveRequestContext;
import org.golive.web.starter.error.ErrorAssert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public class GiftServiceImpl implements IGiftService {

    private static final Logger LOGGER = LoggerFactory.getLogger(GiftServiceImpl.class);

    @DubboReference
    private IGiftConfigRpc giftConfigRpc;
    @DubboReference
    private IGoliveCurrencyAccountRpc goliveCurrencyAccountRpc;
    @Resource
    private MQProducer mqProducer;

    private Cache<Integer, GiftConfigDTO> giftConfigDTOCache = Caffeine.newBuilder().maximumSize(1000).expireAfterWrite(90, TimeUnit.SECONDS).build();

    @Override
    public List<GiftConfigVO> listGift() {
        List<GiftConfigDTO> giftConfigDTOS = giftConfigRpc.queryGiftList();
        return ConvertBeanUtils.convertList(giftConfigDTOS, GiftConfigVO.class);
    }

    @Override
    public boolean send(GiftReqVO giftReqVO) {
        int giftId = giftReqVO.getGiftId();

        // 引入本地缓存caffeine,大大减少对礼物服务的RPC调用
        GiftConfigDTO giftConfigDTO = giftConfigDTOCache.get(giftId, id -> giftConfigRpc.getByGiftId(giftId));

        ErrorAssert.isNotNull(giftConfigDTO, ApiErrorEnum.GIFT_CONFIG_ERROR);
        ErrorAssert.isTrue(!giftReqVO.getReceiverId().equals(giftReqVO.getSenderUserId()), ApiErrorEnum.NOT_SEND_TO_YOURSELF);
        SendGiftMq sendGiftMq = new SendGiftMq();
        sendGiftMq.setUserId(GoliveRequestContext.getUserId());
        sendGiftMq.setGiftId(giftId);
        sendGiftMq.setRoomId(giftReqVO.getRoomId());
        sendGiftMq.setReceiverId(giftReqVO.getReceiverId());
        sendGiftMq.setUrl(giftConfigDTO.getSvgaUrl());
        sendGiftMq.setType(giftReqVO.getType());
        sendGiftMq.setPrice(giftConfigDTO.getPrice());
        // 避免重复消费
        sendGiftMq.setUuid(UUID.randomUUID().toString());

        Message message = new Message();
        message.setTopic(GiftProviderTopicNames.SEND_GIFT);
        message.setBody(JSON.toJSONBytes(sendGiftMq));
        try {
            SendResult sendResult = mqProducer.send(message);
            LOGGER.info("[gift-send] send result is {}", sendResult);
        } catch (Exception e) {
            LOGGER.error("[gift-send] send result is error:", e);
        }
        return true;
    }
}
