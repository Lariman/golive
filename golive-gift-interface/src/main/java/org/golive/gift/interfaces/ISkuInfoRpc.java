package org.golive.gift.interfaces;

import org.golive.gift.dto.SkuDetailInfoDTO;
import org.golive.gift.dto.SkuInfoDTO;

import java.util.List;

public interface ISkuInfoRpc {

    /**
     * 根据主播id查询商品信息
     * @param anchorId
     * @return
     */
    List<SkuInfoDTO> queryByAnchorId(Long anchorId);

    /**
     * 查询商品详情
     * @param skuId
     * @return
     */
    SkuDetailInfoDTO queryBySkuId(Long skuId);
}
