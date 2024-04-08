package org.golive.user.provider.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Maps;
import jakarta.annotation.Resource;
import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.MQProducer;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;
import org.golive.common.interfaces.ConvertBeanUtils;
import org.golive.framework.redis.starter.key.UserProviderCacheKeyBuilder;
import org.golive.user.constants.CacheAsyncDeleteCode;
import org.golive.user.constants.UserProviderTopicNames;
import org.golive.user.dto.UserCacheAsyncDeleteDTO;
import org.golive.user.dto.UserDTO;
import org.golive.user.provider.dao.mapper.IUserMapper;
import org.golive.user.provider.dao.po.UserPO;
import org.golive.user.provider.service.IUserService;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements IUserService {

    @Resource
    private IUserMapper userMapper;

    @Resource
    private RedisTemplate<String, UserDTO> redisTemplate;

    @Resource
    private UserProviderCacheKeyBuilder userProviderCacheKeyBuilder;

    @Resource
    private MQProducer mqProducer;

    /*
     * 根据用户id进行查询
     * */
    @Override
    public UserDTO getByUserId(Long userId) {
        if(userId == null){
            return null;
        }
        // 0.构建key
        String key = userProviderCacheKeyBuilder.buildUserInfoKey(userId);
        // 1.首先在redis中查找
        UserDTO userDTO = redisTemplate.opsForValue().get(key);
        if(userDTO != null){  // 若redis缓存中有数据,则直接返回
            return userDTO;
        }
        // 2.若redis缓存中没有对应数据,则从MySQL数据库查找
        userDTO = ConvertBeanUtils.convert(userMapper.selectById(userId), UserDTO.class);
        if(userDTO != null){  // 若数据库中存在该数据,则直接写入缓存
            redisTemplate.opsForValue().set(key, userDTO, 30, TimeUnit.MINUTES);  // 设置30分钟的缓存过期时间
        }
        // 返回数据
        return userDTO;
    }

    /*
     * 更新用户信息
     * */
    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if(userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        int updateStatus = userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        if(updateStatus > -1){
            // 更新完数据库,实现延迟双删
            String key = userProviderCacheKeyBuilder.buildUserInfoKey(userDTO.getUserId());
            redisTemplate.delete(key);  // 第一次删除
            UserCacheAsyncDeleteDTO userCacheAsyncDeleteDTO = new UserCacheAsyncDeleteDTO();
            userCacheAsyncDeleteDTO.setCode(CacheAsyncDeleteCode.USER_INFO_DELETE.getCode());
            Map<String, Object> jsonParam = new HashMap<>();
            jsonParam.put("userId", userDTO.getUserId());
            userCacheAsyncDeleteDTO.setJson(JSON.toJSONString(jsonParam));
            Message message = new Message();
            message.setBody(JSON.toJSONString(userCacheAsyncDeleteDTO).getBytes());
            message.setTopic(UserProviderTopicNames.CACHE_ASYNC_DELETE_TOPIC);  // 发送消息到消息队列
            // 延迟一秒进行缓存的二次删除
            message.setDelayTimeLevel(1); // 延迟级别, 1为1秒钟
            try{
                mqProducer.send(message); // 第二次删除:延迟双删
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }

    /*
     * 插入用户信息
     * */
    @Override
    public boolean insertOne(UserDTO userDTO) {
        if(userDTO == null || userDTO.getUserId() == null) return false;
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public Map<Long, UserDTO> batchQueryUserInfo(List<Long> userIdList) {
        if(CollectionUtils.isEmpty(userIdList)){
            return Maps.newHashMap();  // 返回hashmap
        }
        userIdList = userIdList.stream().filter(id -> id > 10000).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(userIdList)){
            return Maps.newHashMap();
        }
        // redis
        // 方案1:采用multiGet(),它允许一次传批量参数,只需要一次网络IO就可以将这一批参数查询结果返回
        List<String> keyList = new ArrayList<>();
        userIdList.forEach(userId->{
            keyList.add(userProviderCacheKeyBuilder.buildUserInfoKey(userId));
        });
        // 注意,若缓存查得的数据为空的话,它会传一个null值塞到userDTOList中去,可以做一个判空筛选
        List<UserDTO> userDTOList = redisTemplate.opsForValue().multiGet(keyList).stream().filter(x->x!=null).collect(Collectors.toList());// 需要传List<String>
        // 若查得缓存的数据的个数与请求的id一致,代表完全命中缓存,直接返回结果
        if(!CollectionUtils.isEmpty(userDTOList) && userDTOList.size() == userIdList.size()){
            return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId, x -> x));
        }
        // 取出UserId对象,这样userIdInCacheList就是存在于缓存当中的userId集合
        List<Long> userIdInCacheList = userDTOList.stream().map(UserDTO::getUserId).collect(Collectors.toList());
        // 把没有在缓存当中的userId取出,这些Id需要从数据库来进行查询
        List<Long> userIdNotInCacheList = userIdList.stream().filter(x -> !userIdInCacheList.contains(x)).collect(Collectors.toList());

        /*// 方案2:从userIdList中获取Id,每个Id查一次redis,性能差
        userIdList.forEach(userId -> {
            redisTemplate.opsForValue().get("");
        });*/

        // 直接走MySQL层的话 1. 性能不太好,对MySQL造成很大的压力
        // 底层是使用SHardingJDBC union all关键字去做的,分表情况下,性能低下
        // 可以采用多线程 + 本地内存聚合的方式去做分表的数据查询
        // 将不同表的id分成不同的list交给不同的线程进行查询,最后再在本地内存做归并
        // 思路:多线程查询替换了union all情况
        Map<Long, List<Long>> userIdMap = userIdNotInCacheList.stream().collect(Collectors.groupingBy(userId -> userId % 100));
        List<UserDTO> dbQueryResult = new CopyOnWriteArrayList<>();
        userIdMap.values().parallelStream().forEach(queryUserIdList->{
            dbQueryResult.addAll(ConvertBeanUtils.convertList(userMapper.selectBatchIds(queryUserIdList), UserDTO.class));
        });

        if(!CollectionUtils.isEmpty(dbQueryResult)){
            Map<String, UserDTO> saveCacheMap = dbQueryResult.stream().collect(Collectors.toMap(userDto -> userProviderCacheKeyBuilder.buildUserInfoKey(userDto.getUserId()), x -> x));
            redisTemplate.opsForValue().multiSet(saveCacheMap);
            // 由于multiSet批量写缓存无法设置过期时间,故采用管道方式设置过期时间
            // 管道方式批量传输命令,减少网络IO开销
            redisTemplate.executePipelined(new SessionCallback<Object>() {
                @Override
                public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                    for (String redisKey : saveCacheMap.keySet()) {
                        operations.expire((K)redisKey, createRandomExpireTime(), TimeUnit.SECONDS);  // 随机产生一个30分钟+[1~1000]随机秒的过期时间
                    }
                    return null;
                }
            });
            userDTOList.addAll(dbQueryResult);
        }

        return userDTOList.stream().collect(Collectors.toMap(UserDTO::getUserId, x -> x));
    }

    // 随机产生一个30分钟+[1~1000]随机秒的过期时间
    private int createRandomExpireTime() {
        int time = ThreadLocalRandom.current().nextInt(1000);
        return time + 60 * 30;
    }
}
