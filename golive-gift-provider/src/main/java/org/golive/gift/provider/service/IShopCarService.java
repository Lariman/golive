package org.golive.gift.provider.service;

import org.golive.gift.dto.ShopCarReqDTO;
import org.golive.gift.dto.ShopCarRespDTO;

/**
 * 购物车Service
 */
public interface IShopCarService {

    /**
     * 查看购物车信息
     * @param shopCarReqDTO
     * @return
     */
    ShopCarRespDTO getCarInfo(ShopCarReqDTO shopCarReqDTO);

    /**
     * 添加商品到购物车中
     * @param shopCarReqDTO
     * @return
     */
    Boolean addCar(ShopCarReqDTO shopCarReqDTO);

    /**
     * 移除购物车
     * @param shopCarReqDTO
     * @return
     */
    Boolean removeFromCar(ShopCarReqDTO shopCarReqDTO);

    /**
     * 清理整个购物车
     * @param shopCarReqDTO
     * @return
     */
    Boolean clearShopCar(ShopCarReqDTO shopCarReqDTO);

    /**
     * 修改购物车中某个商品的数量
     * @param shopCarReqDTO
     * @return
     */
    Boolean addCarItemNum(ShopCarReqDTO shopCarReqDTO);
}
