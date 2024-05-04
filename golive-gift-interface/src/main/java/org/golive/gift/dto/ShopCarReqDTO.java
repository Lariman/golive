package org.golive.gift.dto;

import java.io.Serial;
import java.io.Serializable;

public class ShopCarReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4611385542916150690L;

    private Long userId;
    private Long skuId;
    private Integer roomId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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
