package org.golive.gift.dto;

import java.io.Serial;
import java.io.Serializable;

public class PrepareOrderReqDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -1513231730715295850L;

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
