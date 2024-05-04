package org.golive.gift.provider.service;

import java.util.List;

public interface IAnchorShopInfoService {

    /**
     * 根据主播id查询skuId信息
     *
     * @param anchorId
     * @return
     */
    List<Long> querySkuIdByAnchorId(Long anchorId);

    /**
     * 查询有效的主播id
     * @return
     */
    List<Long> queryAllValidAnchorId();
}
