package org.golive.gift.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

public class ShopCarRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4622385542916150690L;

    private Long userId;
    private Integer roomId;
    private List<ShopCarItemRespDTO> shopCarItemRespDTOS;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getRoomId() {
        return roomId;
    }

    public void setRoomId(Integer roomId) {
        this.roomId = roomId;
    }

    public List<ShopCarItemRespDTO> getShopCarItemRespDTOS() {
        return shopCarItemRespDTOS;
    }

    public void setShopCarItemRespDTOS(List<ShopCarItemRespDTO> shopCarItemRespDTOS) {
        this.shopCarItemRespDTOS = shopCarItemRespDTOS;
    }
}
