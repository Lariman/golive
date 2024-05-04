package org.golive.gift.dto;

import java.io.Serial;
import java.io.Serializable;

public class RollBackStockDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4622314242916150690L;

    private Long orderId;
    private Long userId;

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}
