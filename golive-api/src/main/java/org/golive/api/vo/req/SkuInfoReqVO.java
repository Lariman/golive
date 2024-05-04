package org.golive.api.vo.req;

public class SkuInfoReqVO {

    private Long skuId;

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    @Override
    public String toString() {
        return "SkuInfoReqVO{" +
                "skuId=" + skuId +
                '}';
    }
}
