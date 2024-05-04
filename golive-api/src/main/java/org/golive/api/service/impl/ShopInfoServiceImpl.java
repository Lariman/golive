package org.golive.api.service.impl;

import com.alibaba.nacos.common.utils.CollectionUtils;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboReference;
import org.golive.api.error.ApiErrorEnum;
import org.golive.api.service.IShopInfoService;
import org.golive.api.vo.PrepareOrderVO;
import org.golive.api.vo.req.ShopCarReqVO;
import org.golive.api.vo.req.SkuInfoReqVO;
import org.golive.api.vo.resp.ShopCarRespVO;
import org.golive.api.vo.resp.SkuDetailInfoVO;
import org.golive.api.vo.resp.SkuInfoVO;
import org.golive.common.interfaces.utils.ConvertBeanUtils;
import org.golive.gift.dto.*;
import org.golive.gift.interfaces.IShopCarRPC;
import org.golive.gift.interfaces.ISkuInfoRpc;
import org.golive.gift.interfaces.ISkuOrderInfoRPC;
import org.golive.living.interfaces.dto.LivingRoomRespDTO;
import org.golive.living.interfaces.rpc.ILivingRoomRpc;
import org.golive.web.starter.context.GoliveRequestContext;
import org.golive.web.starter.error.BizBaseErrorEnum;
import org.golive.web.starter.error.ErrorAssert;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShopInfoServiceImpl implements IShopInfoService {

    @DubboReference
    private ILivingRoomRpc livingRoomRpc;
    @DubboReference
    private ISkuInfoRpc skuInfoRpc;
    @DubboReference
    private IShopCarRPC shopCarRPC;
    @DubboReference
    private ISkuOrderInfoRPC skuOrderInfoRPC;

    @Override
    public List<SkuInfoVO> queryByRoomId(Integer roomId) {
        LivingRoomRespDTO livingRoomRespDTO = livingRoomRpc.queryByRoomId(roomId);
        ErrorAssert.isNotNull(livingRoomRespDTO, BizBaseErrorEnum.PARAM_ERROR);
        Long anchorId = livingRoomRespDTO.getAnchorId();
        List<SkuInfoDTO> skuInfoDTOS = skuInfoRpc.queryByAnchorId(anchorId);
        ErrorAssert.isTrue(CollectionUtils.isNotEmpty(skuInfoDTOS), BizBaseErrorEnum.PARAM_ERROR);
        return ConvertBeanUtils.convertList(skuInfoDTOS, SkuInfoVO.class);
    }

    @Override
    public SkuDetailInfoVO detail(SkuInfoReqVO skuInfoReqVO) {
        return ConvertBeanUtils.convert(skuInfoRpc.queryBySkuId(skuInfoReqVO.getSkuId()), SkuDetailInfoVO.class);
    }

    @Override
    public Boolean addCar(ShopCarReqVO shopCarReqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(shopCarReqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(GoliveRequestContext.getUserId());
        return shopCarRPC.addCar(shopCarReqDTO);
    }

    @Override
    public ShopCarRespVO getCarInfo(ShopCarReqVO shopCarReqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(shopCarReqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(GoliveRequestContext.getUserId());
        return ConvertBeanUtils.convert(shopCarRPC.getCarInfo(shopCarReqDTO), ShopCarRespVO.class);
    }

    @Override
    public Boolean removeFromCar(ShopCarReqVO shopCarReqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(shopCarReqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(GoliveRequestContext.getUserId());
        return shopCarRPC.removeFromCar(shopCarReqDTO);
    }

    @Override
    public Boolean clearShopCar(ShopCarReqVO shopCarReqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(shopCarReqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(GoliveRequestContext.getUserId());
        return shopCarRPC.clearShopCar(shopCarReqDTO);
    }

    @Override
    public Boolean addCarItemNum(ShopCarReqVO shopCarReqVO) {
        ShopCarReqDTO shopCarReqDTO = ConvertBeanUtils.convert(shopCarReqVO, ShopCarReqDTO.class);
        shopCarReqDTO.setUserId(GoliveRequestContext.getUserId());
        return shopCarRPC.addCarItemNum(shopCarReqDTO);
    }

    @Override
    public SkuPrepareOrderInfoDTO prepareOrder(PrepareOrderVO prepareOrderVO) {
        PrepareOrderReqDTO reqDTO = new PrepareOrderReqDTO();
        reqDTO.setUserId(GoliveRequestContext.getUserId());
        reqDTO.setRoomId(prepareOrderVO.getRoomId());
        return skuOrderInfoRPC.prepareOrder(reqDTO);
    }

    @Override
    public boolean payNow(PrepareOrderVO prepareOrderVO){
        boolean status = skuOrderInfoRPC.payNow(ConvertBeanUtils.convert(prepareOrderVO, PayNowReqDTO.class));
        ErrorAssert.isTrue(status, ApiErrorEnum.PAY_ERROR);
        return status;
    }
}
