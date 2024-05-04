package org.golive.gift.dto;

import java.io.Serial;
import java.io.Serializable;

public class RedPacketConfigRespDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = -4845264558451489561L;

    private Integer id;
    private Integer totalPrice;
    private Integer totalCount;
    private String configCode;
    private String remark;

    @Override
    public String toString() {
        return "RedPacketConfigRespDTO{" +
                "id=" + id +
                ", totalPrice=" + totalPrice +
                ", totalCount=" + totalCount +
                ", configCode=" + configCode +
                ", remark='" + remark + '\'' +
                '}';
    }

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Integer totalPrice) {
        this.totalPrice = totalPrice;
    }

    public Integer getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Integer totalCount) {
        this.totalCount = totalCount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
