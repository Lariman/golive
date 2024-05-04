package org.golive.user.provider.service.impl;

import com.alibaba.fastjson2.JSON;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.golive.user.constants.CacheAsyncDeleteCode;
import org.golive.user.constants.UserProviderTopicNames;
import org.golive.user.constants.UserTagFieldNameConstants;
import org.golive.user.constants.UserTagsEnum;
import org.golive.user.dto.UserCacheAsyncDeleteDTO;
import org.golive.user.dto.UserTagDTO;
import org.golive.user.provider.dao.mapper.IUserTagMapper;
import org.golive.user.provider.dao.po.UserTagPO;
import org.golive.user.provider.service.IUserTagService;
import org.golive.user.utils.TagInfoUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserTagServiceImpl implements IUserTagService {

    @Resource
    private IUserTagMapper userTagMapper;
    @Resource
    private RedisTemplate<String, UserTagDTO> redisTemplate;
    @Resource
    private UserProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private MQProducer mqProducer;


    @Override
    public boolean setTag(Long userId, UserTagsEnum userTagsEnum) {
        // 尝试更新update,但表中没有相应的userId字段,就会有一些问题
        // 优化逻辑:
        // 1. 尝试update,若为true, 则返回
        // 2. 若为false,则分为两种情况:设置了标签、表中没有记录
        // 3. select is null, insert return, update
        boolean updateStatus = userTagMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if(updateStatus){
            deleteUserTagDTOFromRedis(userId);
            return true;
        }

        String setNXKey = cacheKeyBuilder.buildTagLockKey(userId);
        // setNx指令保证只有一个线程进入到MySQL
        String setNxResult = redisTemplate.execute(new RedisCallback<String>() {
            @Override
            public String doInRedis(RedisConnection connection) throws DataAccessException {
                RedisSerializer keySerializer = redisTemplate.getKeySerializer();
                RedisSerializer valueSerializer = redisTemplate.getValueSerializer();

                return (String) connection.execute("set", keySerializer.serialize(setNXKey),
                        valueSerializer.serialize("-1"),
                        "NX".getBytes(StandardCharsets.UTF_8), // SETNX指令：如果key已经存在则设置成功，否则设置失败
                        "EX".getBytes(StandardCharsets.UTF_8),
                        "3".getBytes(StandardCharsets.UTF_8));
            }
        });

        if(!"OK".equals(setNxResult)){
            return false;
        }
        UserTagPO userTagPO = userTagMapper.selectById(userId);
        if(userTagPO != null){
            // 标签重复场景
            return false;
        }

        userTagPO = new UserTagPO();
        userTagPO.setUserId(userId);
        userTagMapper.insert(userTagPO);
        updateStatus = userTagMapper.setTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        System.out.println("测试更新标签成功！！！");
        redisTemplate.delete(setNXKey);
        return updateStatus;
    }

    @Override
    public boolean cancelTag(Long userId, UserTagsEnum userTagsEnum) {
        boolean cancelStatus = userTagMapper.cancelTag(userId, userTagsEnum.getFieldName(), userTagsEnum.getTag()) > 0;
        if(!cancelStatus){
            return false;
        }
        // 删除成功的话，清理缓存
        deleteUserTagDTOFromRedis(userId);
        return true;
    }

    /**
     * 流程：1.先查Redis取得t_user_tag表信息，用UserTagDTO封装
     *      2.判断是否包含，用 & 运算。
     * @param userId ： 用户Id字段，对应可查t_user、t_user_tag表
     * @param userTagsEnum ：枚举类，用于表示用户标签信息
     * @return
     */
    @Override
    public boolean containTag(Long userId, UserTagsEnum userTagsEnum) {
        UserTagDTO userTagDTO = this.queryByUserIdFromRedis(userId);  // 1.从Redis中查寻用户标签，若缓存中没有，则从数据库查，并写回缓存
        if(userTagDTO == null){  // 若缓存和数据库都没有该数据，返回false
            return false;
        }
        String queryFieldName = userTagsEnum.getFieldName();  // 获取用户标签字段
        if(UserTagFieldNameConstants.TAG_INFO_01.equals(queryFieldName)){  // 2.判断是否是对应字段名
            return TagInfoUtils.isContain(userTagDTO.getTagInfo01(), userTagsEnum.getTag());
        }else if(UserTagFieldNameConstants.TAG_INFO_02.equals(queryFieldName)){
            return TagInfoUtils.isContain(userTagDTO.getTagInfo02(), userTagsEnum.getTag());
        }else if(UserTagFieldNameConstants.TAG_INFO_03.equals(queryFieldName)){
            return TagInfoUtils.isContain(userTagDTO.getTagInfo03(), userTagsEnum.getTag());
        }
        return false;
    }

    /**
     * 从redis中删除用户标签对象
     * @param userId
     */
    private void deleteUserTagDTOFromRedis(Long userId){
        String redisKey = cacheKeyBuilder.buildTagKey(userId);  // 构造key
        redisTemplate.delete(redisKey);  // 删除缓存
        UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = new UserCacheAsyncDeleteDTO();
        userCacheAsyncDeleteDTO.setCode(CacheAsyncDeleteCode.USER_TAG_DELETE.getCode());
        Map<String, Object> jsonParam = new HashMap<>();  // 异步删除
        jsonParam.put("userId", userId);
        userCacheAsyncDeleteDTO.setJson(JSON.toJSONString(jsonParam));

        Message message = new Message();
        message.setTopic(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC);  // 发送消息到消息队列
        message.setBody(JSON.toJSONString(userCacheAsyncDeleteDTO).getBytes());
        // 延迟一秒进行缓存的二次删除
        message.setDelayTimeLevel(1); // 延迟级别, 1为1秒钟
        try{
            mqProducer.send(message); // 第二次删除:延迟双删
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 从redis中查询用户标签对象
     * @param userId
     * @return
     */
    private UserTagDTO queryByUserIdFromRedis(Long userId){
        String redisKey = cacheKeyBuilder.buildTagKey(userId);  // 构建Redis的Key：[如:golive-user-provider:userTag:1004]
        UserTagDTO userTagDTO = redisTemplate.opsForValue().get(redisKey);  // 用这个Key查redis
        if(userTagDTO != null){  // 缓存查到数据则直接返回
            return userTagDTO;
        }
        UserTagPO userTagPO = userTagMapper.selectById(userId);  // 缓存没有命中，则从数据库查
        if(userTagPO == null){  // 若为空，则代表数据库没有该数据，直接返回null
            return null;
        }
        userTagDTO = ConvertBeanUtils.convert(userTagPO, UserTagDTO.class);  // PO -> DTO
        redisTemplate.opsForValue().set(redisKey, userTagDTO);  // 将从数据库查到的数据写到缓存中
        redisTemplate.expire(redisKey, 30, TimeUnit.MINUTES);  // 设置30分钟的过期时间
        return userTagDTO;  // 返回对象
    }
}
