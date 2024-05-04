package org.golive.bank.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;

public class PayProductDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 8737032096039095954L;

    private Long id;
    private String name;
    private Integer price;
    private String extra;
    private Integer type;
    private Integer validStatus;
    private Date createTime;
    private Date updateTime;

    @Override
    public String toString() {
        return "PayProductDTO{" +
                "id=" + id +
                ", name=" + name +
                ", price=" + price +
                ", extra='" + extra + '\'' +
                ", type=" + type +
                ", validStatus=" + validStatus +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getValidStatus() {
        return validStatus;
    }

    public void setValidStatus(Integer validStatus) {
        this.validStatus = validStatus;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
