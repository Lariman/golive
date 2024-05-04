package org.golive.gift.dto;

import java.io.Serial;
import java.io.Serializable;

public class SkuPrepareOrderItemInfoDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9005419700814169217L;

    private SkuInfoDTO skuInfoDTO;
    private Integer count;

    public SkuInfoDTO getSkuInfoDTO() {
        return skuInfoDTO;
    }

    public void setSkuInfoDTO(SkuInfoDTO skuInfoDTO) {
        this.skuInfoDTO = skuInfoDTO;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
