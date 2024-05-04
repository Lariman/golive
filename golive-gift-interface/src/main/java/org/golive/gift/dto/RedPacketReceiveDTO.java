package org.golive.gift.dto;

import java.io.Serial;
import java.io.Serializable;

public class RedPacketReceiveDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 9059575343595910533L;

    private Integer price;
    private String notifyMsg;

    public RedPacketReceiveDTO(Integer price, String notifyMsg) {
        this.price = price;
        this.notifyMsg = notifyMsg;
    }


    @Override
    public String toString() {
        return "RedPacketReceiveDTO{" +
                "price=" + price +
                '}';
    }

    public String getNotifyMsg() {
        return notifyMsg;
    }

    public void setNotifyMsg(String notifyMsg) {
        this.notifyMsg = notifyMsg;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }
}
