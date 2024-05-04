package org.golive.gift.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("t_red_packet_config")
public class RedPacketConfigPO {

    @TableId(type = IdType.AUTO)
    private Integer id;
    /**
     * 主播id
     */
    private Long anchorId;
    /**
     * 红包雨开启时间
     */
    private Date startTime;
    /**
     * 红包雨活动结束后统计一共领取红包数量
     */
    private Integer totalGet;
    /**
     * 一共领取红包金额
     */
    private Integer totalGetPrice;
    /**
     * 最大领取金额
     */
    private Integer maxGetPrice;
    /**
     * 状态
     * @see org.golive.common.interfaces.enums.CommonStatusEum
     */
    private Integer status;
    /**
     * 红包雨总金额数
     */
    private Integer totalPrice;
    /**
     * 红包雨总红包数
     */
    private Integer totalCount;
    /**
     * 备注
     */
    private String remark;

    private String configCode;
    private Date createTime;
    private Date updateTime;

    @Override
    public String toString() {
        return "RedPacketConfigPO{" +
                "id=" + id +
                ", anchorId=" + anchorId +
                ", startTime=" + startTime +
                ", totalGet=" + totalGet +
                ", totalGetPrice=" + totalGetPrice +
                ", maxGetPrice=" + maxGetPrice +
                ", status=" + status +
                ", totalPrice=" + totalPrice +
                ", totalCount=" + totalCount +
                ", remark='" + remark + '\'' +
                ", configCode='" + configCode + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Long getAnchorId() {
        return anchorId;
    }

    public void setAnchorId(Long anchorId) {
        this.anchorId = anchorId;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Integer getTotalGet() {
        return totalGet;
    }

    public void setTotalGet(Integer totalGet) {
        this.totalGet = totalGet;
    }

    public Integer getTotalGetPrice() {
        return totalGetPrice;
    }

    public void setTotalGetPrice(Integer totalGetPrice) {
        this.totalGetPrice = totalGetPrice;
    }

    public Integer getMaxGetPrice() {
        return maxGetPrice;
    }

    public void setMaxGetPrice(Integer maxGetPrice) {
        this.maxGetPrice = maxGetPrice;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
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

    public String getConfigCode() {
        return configCode;
    }

    public void setConfigCode(String configCode) {
        this.configCode = configCode;
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
