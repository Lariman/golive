package org.golive.api.vo.req;

public class ShopCarReqVO {

    private Long skuId;
    private Integer roomId;

    @Override
    public String toString() {
        return "ShopCarReqVO{" +
                "skuId=" + skuId +
                ", roomId=" + roomId +
                '}';
    }

    public Long getSkuId() {
        return skuId;
    }

    public void setSkuId(Long skuId) {
        this.skuId = skuId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }
}
