package org.golive.framework.redis.starter.key;

import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;


/**
 * @Author idea
 * @Date: Created in 10:23 2023/6/20
 * @Description
 */
@Configuration
@Conditional(RedisKeyLoadMatch.class)
public class GiftProviderCacheKeyBuilder extends RedisKeyBuilder {

    private static String GIFT_CONFIG_CACHE = "gift_config_cache";
    private static String GIFT_LIST_CACHE = "gift_list_cache";
    private static String GIFT_CONSUME_KEY = "gift_consume_key";
    private static String GIFT_LIST_LOCK = "gift_list_lock";
    private static String LIVING_PK_KEY = "living_pk_key";
    private static String LIVING_PK_SEND_SEQ = "living_pk_send_seq";
    private static String LIVING_PK_IS_OVER = "living_pk_is over";
    private static String RED_PACKET_LIST = "red_packet_list";
    private static String RED_PACKET_INIT_LOCK = "red_packet_init_lock";
    private static String RED_PACKET_TOTAL_GET_CACHE = "red_packet_total_get_cache";
    private static String RED_PACKET_TOTAL_GET_PRICE_CACHE = "red_packet_total_get_price_cache";
    private static String MAX_GET_PRICE_CACHE = "max_get_price_cache";
    private static String USER_TOTAL_GET_PRICE_CACHE = "user_total_get_price_cache";
    private static String RED_PACKET_PREPARE_SUCCESS = "red_packet_prepare_success";
    private static String RED_PACKET_NOTIFY = "red_packet_notify";
    private static String SKU_DETAIL_KEY = "sku_detail";
    private static String SKU_STOCK = "sku_stock";
    private static String SHOP_CAR = "shop_car";
    private static String SKU_STOCK_SYNC_LOCK = "sku_stock_sync_lock";
    private static String SKU_ORDER = "sku_order";
    private static String SKU_ORDER_INFO = "sku_order_info";

    public String buildSkuOrderInfo(Long orderId){
        return super.getPrefix() + SKU_ORDER_INFO + super.getSplitItem() + orderId;
    }

    public String buildSkuOrder(Long userId, Integer roomId){
        return super.getPrefix() + SKU_ORDER + super.getSplitItem() + userId + super.getSplitItem() + roomId;
    }

    public String buildSkuStockLock(){
        return super.getPrefix() + SKU_STOCK_SYNC_LOCK + super.getSplitItem();
    }

    public String buildSkuStock(Long skuId){
        return super.getPrefix() + SKU_STOCK + super.getSplitItem() + skuId;
    }

    public String buildUserShopCar(Long userId, Integer roomId){
        return super.getPrefix() + SHOP_CAR + super.getSplitItem() + userId + super.getSplitItem() + roomId;
    }

    public String buildSkuDetailKey(Long skuId){
        return super.getPrefix() + SKU_DETAIL_KEY + super.getSplitItem() + skuId;
    }

    public String buildRedPacketNotify(String code){
        return super.getPrefix() + RED_PACKET_NOTIFY + super.getSplitItem() + code;
    }

    public String buildRedPacketPrepareSuccess(String code){
        return super.getPrefix() + RED_PACKET_PREPARE_SUCCESS + super.getSplitItem() + code;
    }

    public String buildUserTotalGetPriceCache(Long userId){
        return super.getPrefix() + USER_TOTAL_GET_PRICE_CACHE + super.getSplitItem() + userId;
    }

    public String buildRedPacketTotalGet(String code) {
        return super.getPrefix() + RED_PACKET_TOTAL_GET_CACHE + super.getSplitItem() + code;
    }

    public String buildRedPacketTotalGetPrice(String code) {
        return super.getPrefix() + RED_PACKET_TOTAL_GET_PRICE_CACHE + super.getSplitItem() + code;
    }

    public String buildRedPacketMaxGetPrice(String code) {
        return super.getPrefix() + MAX_GET_PRICE_CACHE + super.getSplitItem() + code;
    }

    public String buildRedPacketInitLock(String code) {
        return super.getPrefix() + RED_PACKET_INIT_LOCK + super.getSplitItem() + code;
    }

    public String buildRedPacketList(String code) {
        return super.getPrefix() + RED_PACKET_LIST + super.getSplitItem() + code;
    }

    public String buildLivingPkIsOver(Integer roomId) {
        return super.getPrefix() + LIVING_PK_IS_OVER + super.getSplitItem() + roomId;
    }

    public String buildLivingPkSendSeq(Integer roomId) {
        return super.getPrefix() + LIVING_PK_SEND_SEQ + super.getSplitItem() + roomId;
    }

    public String buildLivingPkKey(Integer roomId) {
        return super.getPrefix() + LIVING_PK_KEY + super.getSplitItem() + roomId;
    }

    public String buildGiftConsumeKey(String uuid) {
        return super.getPrefix() + GIFT_CONSUME_KEY + super.getSplitItem() + uuid;
    }

    public String buildGiftConfigCacheKey(int giftId) {
        return super.getPrefix() + GIFT_CONFIG_CACHE + super.getSplitItem() + giftId;
    }

    public String buildGiftListCacheKey() {
        return super.getPrefix() + GIFT_LIST_CACHE;
    }

    public String buildGiftListLockCacheKey() {
        return super.getPrefix() + GIFT_LIST_LOCK;
    }
}
