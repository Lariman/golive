package org.golive.gift.provider.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.Resource;
import org.golive.common.interfaces.enums.CommonStatusEum;
import org.golive.gift.provider.dao.mapper.AnchorShopInfoMapper;
import org.golive.gift.provider.dao.po.AnchorShopInfoPO;
import org.golive.gift.provider.service.IAnchorShopInfoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnchorShopInfoServiceImpl implements IAnchorShopInfoService {

    @Resource
    private AnchorShopInfoMapper anchorShopInfoMapper;

    @Override
    public List<Long> querySkuIdByAnchorId(Long anchorId) {
        LambdaQueryWrapper<AnchorShopInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnchorShopInfoPO::getAnchorId, anchorId);
        queryWrapper.eq(AnchorShopInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return anchorShopInfoMapper.selectList(queryWrapper).stream().map(AnchorShopInfoPO::getSkuId).collect(Collectors.toList());
    }

    @Override
    public List<Long> queryAllValidAnchorId() {
        LambdaQueryWrapper<AnchorShopInfoPO> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(AnchorShopInfoPO::getStatus, CommonStatusEum.VALID_STATUS.getCode());
        return anchorShopInfoMapper.selectList(queryWrapper).stream().map(AnchorShopInfoPO::getAnchorId).collect(Collectors.toList());
    }
}
