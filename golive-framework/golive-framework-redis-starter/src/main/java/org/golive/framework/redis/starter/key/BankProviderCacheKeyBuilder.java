package org.golive.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;

@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class BankProviderCacheKeyBuilder extends RedisKeyBuilder{

    private static String BALANCE_CACHE = "balance_cache";

    private static String PAY_PRODUCT_CACHE = "pay_product_cache";

    private static String PAY_PRODUCT_ITEM_CACHE = "pay_product_item_cache";

    public String buildPayProductItemCache(Integer productId){
        return super.getPrefix() + PAY_PRODUCT_ITEM_CACHE + super.getSplitItem() + productId;
    }

    /**
     * 按照产品的类型进行检索
     * @param type
     * @return
     */
    public String buildPayProductCache(Integer type){
        return super.getPrefix() + PAY_PRODUCT_CACHE + super.getSplitItem() + type;
    }

    /**
     * 构建用户余额cache key
     * @param userId
     * @return
     */
    public String buildUserBalance(Long userId){
        return super.getPrefix() + BALANCE_CACHE + super.getSplitItem() + userId;
    }
}
