package org.golive.gift.provider.rpc;

import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;
import org.golive.gift.dto.ShopCarReqDTO;
import org.golive.gift.dto.ShopCarRespDTO;
import org.golive.gift.interfaces.IShopCarRPC;
import org.golive.gift.provider.service.IShopCarService;

@DubboService
public class ShopCarRpcImpl implements IShopCarRPC {

    @Resource
    private IShopCarService shopCarService;

    @Override
    public ShopCarRespDTO getCarInfo(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.getCarInfo(shopCarReqDTO);
    }

    @Override
    public Boolean addCar(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.addCar(shopCarReqDTO);
    }

    @Override
    public Boolean removeFromCar(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.removeFromCar(shopCarReqDTO);
    }

    @Override
    public Boolean clearShopCar(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.clearShopCar(shopCarReqDTO);
    }

    @Override
    public Boolean addCarItemNum(ShopCarReqDTO shopCarReqDTO) {
        return shopCarService.addCarItemNum(shopCarReqDTO);
    }
}
