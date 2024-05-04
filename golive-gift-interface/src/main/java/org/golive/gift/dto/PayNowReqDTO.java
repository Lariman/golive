package org.golive.gift.dto;

import java.io.Serial;
import java.io.Serializable;

public class PayNowReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 4714554906835449778L;

    private Long userId;
    private Integer roomId;

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
}
