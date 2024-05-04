package org.golive.gift.provider.config;

import jakarta.annotation.Resource;
import org.golive.framework.redis.starter.key.GiftProviderCacheKeyBuilder;
import org.golive.gift.interfaces.ISkuStockInfoRPC;
import org.golive.gift.provider.service.IAnchorShopInfoService;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
public class RefreshStockNumConfig implements InitializingBean {

    @Resource
    private ISkuStockInfoRPC stockInfoRPC;
    @Resource
    private IAnchorShopInfoService anchorShopInfoService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private GiftProviderCacheKeyBuilder cacheKeyBuilder;

    private ScheduledThreadPoolExecutor schedulePool = new ScheduledThreadPoolExecutor(1);


    @Override
    public void afterPropertiesSet() throws Exception {
        // 15秒刷新一次
        schedulePool.scheduleWithFixedDelay(new RefreshStockNumJob(), 3000, 15000, TimeUnit.MILLISECONDS);
    }

    class RefreshStockNumJob implements Runnable{

        @Override
        public void run() {
            boolean lockStatus = redisTemplate.opsForValue().setIfAbsent(cacheKeyBuilder.buildSkuStockLock(), 1, 14, TimeUnit.SECONDS);
            if(lockStatus){
                List<Long> anchorIdList = anchorShopInfoService.queryAllValidAnchorId();
                for (Long anchorId : anchorIdList) {
                    stockInfoRPC.syncStockNumToMySQL(anchorId);
                }
            }
        }
    }
}
