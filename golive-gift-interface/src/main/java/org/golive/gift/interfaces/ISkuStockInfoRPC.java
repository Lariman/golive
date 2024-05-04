package org.golive.gift.interfaces;

public interface ISkuStockInfoRPC {

    /**
     * 根据skuId更新库存值
     * @param skuId
     * @return
     */
    boolean decrStockNumBySkuId(Long skuId, Integer num);

    /**
     * 根据skuId扣减库存值
     * @param skuId
     * @param num
     * @return
     */
    boolean decrStockNumBySkuIdV2(Long skuId, Integer num);

    // 1.定义 库存(mysql) -> redis (预热同步)

    /**
     * 预热库存信息
     * @return
     */
    boolean prepareStockInfo(Long anchorId);

    // 提供基础的缓存查询接口

    /**
     * 基础的缓存查询接口
     * @param skuId
     * @return
     */
    Integer queryStockNum(Long skuId);
    // 设计一个接口用于同步redis值到mysql中(定时任务执行,本地定时任务去完成同步行为)

    /**
     * 同步库存数据到MySQL
     * @param anchorId
     * @return
     */
    boolean syncStockNumToMySQL(Long anchorId);

    // 3.库存扣减设计lua脚本

    // 4.库存扣减成功后,生成待支付订单(MQ)


}
