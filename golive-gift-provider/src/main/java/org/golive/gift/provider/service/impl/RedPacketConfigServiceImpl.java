package org.golive.gift.provider.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.common.interfaces.enums.CommonStatusEum;
import org.golive.common.interfaces.utils.ListUtils;
import org.golive.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.golive.gift.dto.RedPacketConfigReqDTO;
import org.golive.gift.dto.RedPacketReceiveDTO;
import org.golive.gift.provider.dao.mapper.RedPacketConfigMapper;
import org.golive.gift.provider.dao.po.RedPacketConfigPO;
import org.golive.gift.provider.service.IRedPacketConfigService;
import org.golive.im.constants.AppIdEnum;
import org.golive.im.dto.ImMsgBody;
import org.golive.im.router.interfaces.constants.ImMsgBizCodeEnum;
import org.golive.im.router.interfaces.rpc.ImRouterRpc;
import org.golive.living.interfaces.dto.LivingRoomReqDTO;
import org.golive.living.interfaces.rpc.ILivingRoomRpc;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class RedPacketConfigServiceImpl implements IRedPacketConfigService {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedPacketConfigServiceImpl.class);

    @Resource
    private RedPacketConfigMapper redPacketConfigMapper;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;
    @DubboReference
    private ImRouterRpc routerRpc;
    @DubboReference
    private ILivingRoomRpc livingRoomRpc;

    @Override
    public RedPacketConfigPO queryByAnchorId(Long anchorId) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getAnchorId, anchorId);
        queryWrapper.eq(RedPacketConfigPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public RedPacketConfigPO queryByConfigCode(String configCode) {
        LambdaQueryWrapper<RedPacketConfigPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(RedPacketConfigPO::getCreateTime, configCode);
        queryWrapper.eq(RedPacketConfigPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        queryWrapper.orderByDesc(RedPacketConfigPO::getCreateTime);
        queryWrapper.last("limit 1");
        return redPacketConfigMapper.selectOne(queryWrapper);
    }

    @Override
    public boolean addOne(RedPacketConfigPO redPacketConfigPO) {
        redPacketConfigPO.setConfigCode(UUID.randomUUID().toString());
        return redPacketConfigMapper.insert(redPacketConfigPO) > 0;
    }


    @Override
    public boolean updateById(RedPacketConfigPO redPacketConfigPO) {
        return redPacketConfigMapper.updateById(redPacketConfigPO) > 0;
    }

    @Override
    public boolean prepareRedPacket(Long anchorId) {
        // 防止重复生成,以及错误参数传递情况
        RedPacketConfigPO configPO = this.queryByAnchorId(anchorId);
        if(configPO == null){
            return false;
        }
        boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKeyBuilder.buildRedPacketInitLock(configPO.getConfigCode()), 1, 3, TimeUnit.SECONDS);
        if(!lockStatus){
            return false;
        }
        Integer totalCount = configPO.getTotalCount();
        Integer totalPrice = configPO.getTotalPrice();
        String code = configPO.getConfigCode();
        List<Integer> priceList = this.createRedPacketPriceList(totalPrice, totalCount);
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        // redis输入输出缓冲区,
        List<List<Integer>> splitPriceList = (List<List<Integer>>) ListUtils.splitList(priceList, 100);
        for (List<Integer> priceItemList : splitPriceList) {
            redisTemplate.opsForList().leftPushAll(cacheKey, priceItemList);
        }
        redisTemplate.expire(cacheKey, 1, TimeUnit.DAYS);
        configPO.setStatus(CommonStatusEum.INVALID_STATUS.getCode());
        this.updateById(configPO);
        redisTemplate.opsForValue().set(cacheKeyBuilder.buildRedPacketPrepareSuccess(code), 1, 1, TimeUnit.DAYS);
        return true;
    }

    @Override
    public RedPacketReceiveDTO receiveRedPacket(RedPacketConfigReqDTO reqDTO) {
        String code = reqDTO.getRedPacketConfigCode();
        String cacheKey = cacheKeyBuilder.buildRedPacketList(code);
        Object priceObj = redisTemplate.opsForList().rightPop(cacheKey);
        if(priceObj == null){
            return null;
        }
        String totalGetCacheKey = cacheKeyBuilder.buildRedPacketTotalGet(code);
        String totalGetPriceCacheKey = cacheKeyBuilder.buildRedPacketTotalGetPrice(code);
        redisTemplate.opsForValue().increment(cacheKeyBuilder.buildUserTotalGetPriceCache(reqDTO.getUserId()), (int) priceObj);
        redisTemplate.opsForValue().increment(totalGetCacheKey);
        redisTemplate.expire(totalGetCacheKey, 1, TimeUnit.DAYS);
        redisTemplate.opsForValue().increment(totalGetPriceCacheKey, (int) priceObj);
        redisTemplate.expire(totalGetPriceCacheKey, 1, TimeUnit.DAYS);
        //todo lua脚本去记录最大值

        LOGGER.info("[receiveRedPacket] code is {}, priceObj is {}", code, priceObj);
        return new RedPacketReceiveDTO((Integer) priceObj, "恭喜领取红包" + (Integer) priceObj + "golive币");
    }



    @Override
    public Boolean startRedPacket(RedPacketConfigReqDTO reqDTO) {
        String code = reqDTO.getRedPacketConfigCode();
        if(!redisTemplate.hasKey(cacheKeyBuilder.buildRedPacketPrepareSuccess(code))){
            return false;
        }
        String notifySuccessCache = cacheKeyBuilder.buildRedPacketNotify(code);
        if(!redisTemplate.hasKey(notifySuccessCache)){
            return false;
        }
        RedPacketConfigPO configPO = this.queryByConfigCode(code);
        // 广播im事件
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("redPacketConfig", JSON.toJSONString(configPO));
        LivingRoomReqDTO livingRoomReqDTO = new LivingRoomReqDTO();
        livingRoomReqDTO.setRoomId(reqDTO.getRoomId());
        livingRoomReqDTO.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
        List<Long> userIdList = livingRoomRpc.queryUserIdByRoomId(livingRoomReqDTO);
        if(CollectionUtils.isEmpty(userIdList)){
            return false;
        }
        this.batchSendImMsg(userIdList, ImMsgBizCodeEnum.START_RED_PACKET, jsonObject);
        // configPO.setStatus(RedPacketStatusCodeEnum.HAS_SEND.getCode());
        // this.updateById(configPO);
        redisTemplate.opsForValue().set(notifySuccessCache, 1, 1, TimeUnit.DAYS);
        return true;
    }

    /**
     * 批量发送im消息
     *
     * @param userIdList
     * @param imMsgBizCodeEnum
     * @param jsonObject
     */
    private void batchSendImMsg(List<Long> userIdList, ImMsgBizCodeEnum imMsgBizCodeEnum, JSONObject jsonObject) {
        List<ImMsgBody> imMsgBodies = userIdList.stream().map(userId -> {
            ImMsgBody imMsgBody = new ImMsgBody();
            imMsgBody.setAppId(AppIdEnum.GOLIVE_BIZ.getCode());
            imMsgBody.setBizCode(imMsgBizCodeEnum.getCode());
            imMsgBody.setUserId(userId);
            imMsgBody.setData(jsonObject.toJSONString());
            return imMsgBody;
        }).collect(Collectors.toList());
        routerRpc.batchSendMsg(imMsgBodies);
    }

    /**
     * 生成红包金额List集合数据
     * @param totalPrice
     * @param totalCount
     * @return
     */
    private List<Integer> createRedPacketPriceList(Integer totalPrice, Integer totalCount) {
        List<Integer> redPacketPriceList = new ArrayList<>(totalCount);
        for(int i = 0; i < totalCount; i++){
            // 如果是最后一个红包
            if(totalCount == i + 1){
                redPacketPriceList.add(totalPrice);
                break;
            }
            int maxLimit = (totalPrice / (totalCount - i)) * 2;
            int currentPrice = ThreadLocalRandom.current().nextInt(1, maxLimit);
            totalPrice -= currentPrice;
            redPacketPriceList.add(currentPrice);
        }
        return redPacketPriceList;
    }

}
