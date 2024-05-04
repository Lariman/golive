package org.golive.living.provider.config;

import jakarta.annotation.Resource;
import org.golive.framework.redis.starter.key.LivingProviderCacheKeyBuilder;
import org.golive.living.interfaces.constants.LivingRoomTypeEnum;
import org.golive.living.interfaces.dto.LivingRoomRespDTO;
import org.golive.living.provider.service.ILivingRoomService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RefreshLivingRoomListJob implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(RefreshLivingRoomListJob.class);

    @Resource
    private LivingProviderCacheKeyBuilder cacheKeyBuilder;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ILivingRoomService livingRoomService;

    private ScheduledThreadPoolExecutor schedulePool = new ScheduledThreadPoolExecutor(1);

    @Override
    public void afterPropertiesSet() throws Exception {
        // 一秒钟刷新一次直播间列表数据
        schedulePool.scheduleWithFixedDelay(new RefreshCacheListJob(), 3000, 1000, TimeUnit.MILLISECONDS);
    }

    class RefreshCacheListJob implements Runnable{

        @Override
        public void run() {
            String cacheKey = cacheKeyBuilder.buildRefreshLivingRoomListLock();
            // 这把锁等它自动过期, 1秒过期时间
            boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKey, 1, 1, TimeUnit.SECONDS);
            if(lockStatus){
                LOGGER.debug("[RefreshLivingRoomListJob] starting 加载db记录的直播间到redis中");
                refreshDBToRedis(LivingRoomTypeEnum.DEFAULT_LIVING_ROOM.getCode());
                refreshDBToRedis(LivingRoomTypeEnum.PK_LIVING_ROOM.getCode());
                LOGGER.debug("[RefreshLivingRoomListJob] end 加载db中记录的直播间到redis中");
            }
        }
    }

    private void refreshDBToRedis(Integer type){
        String cacheKey = cacheKeyBuilder.buildLivingRoomList(type);
        List<LivingRoomRespDTO> resultList = livingRoomService.listAllLivingRoomFromDB(type);
        if(CollectionUtils.isEmpty(resultList)){
            redisTemplate.delete(cacheKey);
            return;
        }
        String tempListName = cacheKey + "_temp";
        // 按照查询出来的顺序插入到list集合中
        for (LivingRoomRespDTO livingRoomRespDTO : resultList) {
            redisTemplate.opsForList().rightPush(tempListName, livingRoomRespDTO);
        }
        // 正在访问的list集合, del -> leftPush
        // 直接修改重命名这个list,减少阻塞影响
        redisTemplate.rename(tempListName, cacheKey);  // rename():删除新key,
        redisTemplate.delete(tempListName);
    }


}
