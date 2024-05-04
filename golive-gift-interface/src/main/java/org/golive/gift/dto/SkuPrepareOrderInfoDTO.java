package org.golive.gift.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class SkuPrepareOrderInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4205419700814169217L;

    private List<SkuPrepareOrderItemInfoDTO> skuPrepareOrderItemInfoDTOS;
    private Integer totalPrice;

    public List<SkuPrepareOrderItemInfoDTO> getSkuPrepareOrderItemInfoDTOS() {
        return skuPrepareOrderItemInfoDTOS;
    }

    public void setSkuPrepareOrderItemInfoDTOS(List<SkuPrepareOrderItemInfoDTO> skuPrepareOrderItemInfoDTOS) {
        this.skuPrepareOrderItemInfoDTOS = skuPrepareOrderItemInfoDTOS;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }
}
