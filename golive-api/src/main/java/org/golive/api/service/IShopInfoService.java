package org.golive.api.service;

import org.golive.api.vo.PrepareOrderVO;
import org.golive.api.vo.req.ShopCarReqVO;
import org.golive.api.vo.req.SkuInfoReqVO;
import org.golive.api.vo.resp.ShopCarRespVO;
import org.golive.api.vo.resp.SkuDetailInfoVO;
import org.golive.api.vo.resp.SkuInfoVO;
import org.golive.gift.dto.ShopCarReqDTO;
import org.golive.gift.dto.ShopCarRespDTO;
import org.golive.gift.dto.SkuPrepareOrderInfoDTO;

import java.util.List;

public interface IShopInfoService {

    /**
     * 根据直播间id查询商品信息
     * @param roomId
     * @return
     */
    List<SkuInfoVO> queryByRoomId(Integer roomId);

    /**
     * 查询商品详情
     *
     * @param skuInfoReqVO
     * @return
     */
    SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO);

    /**
     * 添加商品到购物车
     * @return
     */
    Boolean addCar(ShopCarReqVO shopCarReqVO);

    /**
     * 查看购物车信息
     * @param shopCarReqVO
     * @return
     */
    ShopCarRespVO getCarInfo(ShopCarReqVO shopCarReqVO);


    /**
     * 移除购物车
     * @param shopCarReqVO
     * @return
     */
    Boolean removeFromCar(ShopCarReqVO shopCarReqVO);

    /**
     * 清理整个购物车
     * @param shopCarReqVO
     * @return
     */
    Boolean clearShopCar(ShopCarReqVO shopCarReqVO);

    /**
     * 修改购物车中某个商品的数量
     * @param shopCarReqVO
     * @return
     */
    Boolean addCarItemNum(ShopCarReqVO shopCarReqVO);

    /**
     * 预下单接口
     * @param prepareOrderVO
     * @return
     */
    SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO prepareOrderVO);

    /**
     * 立即支付
     * @param prepareOrderVO
     * @return
     */
    boolean payNow(PrepareOrderVO prepareOrderVO);
}
