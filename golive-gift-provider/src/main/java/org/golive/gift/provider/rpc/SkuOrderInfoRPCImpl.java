package org.golive.gift.provider.rpc;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.common.utils.CollectionUtils;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.golive.bank.constants.OrderStatusEnum;
import org.golive.bank.interfaces.IGoliveCurrencyAccountRpc;
import org.golive.common.interfaces.topic.GiftProviderTopicNames;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.gift.constants.SkuOrderInfoEnum;
import org.golive.gift.dto.*;
import org.golive.gift.interfaces.ISkuOrderInfoRPC;
import org.golive.gift.provider.dao.po.SkuInfoPO;
import org.golive.gift.provider.dao.po.SkuOrderInfoPO;
import org.golive.gift.provider.dao.po.SkuStockInfoPO;
import org.golive.gift.provider.service.IShopCarService;
import org.golive.gift.provider.service.ISkuInfoService;
import org.golive.gift.provider.service.ISkuOrderInfoService;
import org.golive.gift.provider.service.ISkuStockInfoService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@DubboService
public class SkuOrderInfoRPCImpl implements ISkuOrderInfoRPC {

    @Resource
    private ISkuOrderInfoService skuOrderInfoService;
    @Resource
    private IShopCarService shopCarService;
    @Resource
    private ISkuInfoService skuInfoService;
    @Resource
    private ISkuStockInfoService skuStockInfoService;
    @Resource
    private MQProducer mqProducer;
    @Resource
    private IGoliveCurrencyAccountRpc accountRpc;

    @Override
    public SkuOrderInfoRespDTO queryByUserIdAndRoomId(Long userId, Integer roomId) {
        return skuOrderInfoService.queryByUserIdAndRoomId(userId, roomId);
    }

    @Override
    public boolean insertOne(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        return skuOrderInfoService.insertOne(skuOrderInfoReqDTO) != null;
    }

    @Override
    public boolean updateOrderStatus(SkuOrderInfoReqDTO skuOrderInfoReqDTO) {
        return skuOrderInfoService.updateOrderStatus(skuOrderInfoReqDTO);
    }

    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderReqDTO prepareOrderReqDTO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(prepareOrderReqDTO, ShopCarReqDTO.class);
        ShopCarRespDTO shopCarRespDTO = shopCarService.getCarInfo(shopCarReqDTO);
        List<ShopCarItemRespDTO> carItemList = shopCarRespDTO.getShopCarItemRespDTOS();
        if(CollectionUtils.isEmpty(carItemList)){
            return null;
        }
        List<Long> skuIdList = carItemList.stream().map(item -> item.getSkuInfoDTO().getSkuId()).collect(Collectors.toList());
        // 核心知识点 库存回滚
        boolean status = false;
        for (Long skuId : skuIdList) {
            status = skuStockInfoService.decrStockNumBySkuIdV2(skuId, 1);
        }

        SkuOrderInfoReqDTO skuOrderInfoReqDTO = new SkuOrderInfoReqDTO();
        skuOrderInfoReqDTO.setSkuIdList(skuIdList);
        skuOrderInfoReqDTO.setStatus(SkuOrderInfoEnum.PREPARE_PAY.getCode());
        skuOrderInfoReqDTO.setUserId(prepareOrderReqDTO.getUserId());
        skuOrderInfoReqDTO.setRoomId(prepareOrderReqDTO.getRoomId());
        SkuOrderInfoPO skuOrderInfoPO = skuOrderInfoService.insertOne(skuOrderInfoReqDTO);
        // 库存回滚的mq延迟消息发送
        stockRollBackHandler(skuOrderInfoPO.getUserId(), skuOrderInfoPO.getId());

        shopCarService.removeFromCar(shopCarReqDTO);
        List<ShopCarItemRespDTO> shopCarItemRespDTOS = shopCarRespDTO.getShopCarItemRespDTOS();
        List<SkuPrepareOrderItemInfoDTO> itemList = new ArrayList<>();
        Integer totalPrice = 0;
        for (ShopCarItemRespDTO shopCarItemRespDTO : shopCarItemRespDTOS) {
            SkuPrepareOrderItemInfoDTO orderItemInfoDTO = new SkuPrepareOrderItemInfoDTO();
            orderItemInfoDTO.setSkuInfoDTO(shopCarItemRespDTO.getSkuInfoDTO());
            orderItemInfoDTO.setCount(shopCarItemRespDTO.getCount());
            itemList.add(orderItemInfoDTO);
            totalPrice = totalPrice + shopCarItemRespDTO.getSkuInfoDTO().getSkuPrice();
        }
        SkuPrepareOrderInfoDTO skuPrepareOrderInfoDTO = new SkuPrepareOrderInfoDTO();
        skuPrepareOrderInfoDTO.setSkuPrepareOrderItemInfoDTOS(itemList);
        skuPrepareOrderInfoDTO.setTotalPrice(totalPrice);
        return skuPrepareOrderInfoDTO;
    }

    @Override
    public boolean payNow(PayNowReqDTO payNowReqDTO) {
        SkuOrderInfoRespDTO orderInfoRespDTO = skuOrderInfoService.queryByUserIdAndRoomId(payNowReqDTO.getUserId(), payNowReqDTO.getRoomId());
        if(OrderStatusEnum.WAITING_PAY.getCode().equals(orderInfoRespDTO.getSkuIdList())){
            return false;
        }
        List<Long> skuIdList = Arrays.stream(orderInfoRespDTO.getSkuIdList().split(",")).map(skuId -> Long.valueOf(skuId)).collect(Collectors.toList());
        List<SkuInfoPO> skuInfoPOS = skuInfoService.queryBySkuIds(skuIdList).stream().collect(Collectors.toList());
        Integer skuPrice = 0;
        for (SkuInfoPO skuInfoPO : skuInfoPOS) {
            skuPrice = skuPrice + skuInfoPO.getSkuPrice();
        }
        Integer currentBalance = accountRpc.getBalance(payNowReqDTO.getUserId());
        if(currentBalance - skuPrice < 0){
            return false;
        }
        SkuOrderInfoReqDTO updateDto = new SkuOrderInfoReqDTO();
        updateDto.setId(orderInfoRespDTO.getId());
        updateDto.setStatus(orderInfoRespDTO.getStatus());
        updateDto.setRoomId(orderInfoRespDTO.getRoomId());
        updateDto.setUserId(orderInfoRespDTO.getUserId());
        this.updateOrderStatus(updateDto);
        accountRpc.decr(payNowReqDTO.getUserId(), skuPrice);
        ShopCarReqDTO shopCarReqDTO = new ShopCarReqDTO();
        shopCarReqDTO.setUserId(orderInfoRespDTO.getUserId());
        shopCarReqDTO.setRoomId(orderInfoRespDTO.getRoomId());
        shopCarService.clearShopCar(shopCarReqDTO);
        return true;
    }

    private void stockRollBackHandler(Long userId, Long orderId){
        // 订单超时概念,21:00生成订单,21:30分若未支付订单会自动关闭,21:25分的时候会有订单提醒功能.
        // 方案1.定时任务,select * from t_sku_order_info where status = 1 and create_time < '2023-10-16 23:00'。制定索引，数据量非常高的时候，扫描表会非常耗时
        // 方案2.redis的过期回调，key过期之后，会有一个回调通知，比如orderid：1001，将其作为redis的key存起来。若ttl到期，则会回调到订阅方。【弊端】：回调并不是高可靠的，可能会丢失
        // 方案3.RocketMQ 延迟消息，基于时间轮做
        //     将扣减库存的信息，利用mq发送出去，在延迟回调处进行校验。
        RollBackStockDTO rollBackStockDTO = new RollBackStockDTO();
        rollBackStockDTO.setUserId(userId);
        rollBackStockDTO.setOrderId(orderId);
        Message message = new Message();
        message.setTopic(GiftProviderTopicNames.ROLL_BACK_STOCK);
        message.setBody(JSON.toJSONBytes(rollBackStockDTO));
        // messageDelayLevel = 1s 5s 10s(3) 30s 1m 2m 3m 4m 5m 6m 7m 8m 9m 10m 20m 30m 1h 2h
        message.setDelayTimeLevel(16);
        try {
            mqProducer.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
