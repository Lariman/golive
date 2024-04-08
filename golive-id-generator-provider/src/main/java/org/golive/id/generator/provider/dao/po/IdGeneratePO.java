package org.golive.id.generator.provider.dao.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.util.Date;

@TableName("t_id_generate_config")
public class IdGeneratePO {

    @TableId(type = IdType.AUTO)
    private Integer id;

    /*
    * ID备注描述
    * */
    private String remark;

    /*
    * 初始化值
    * */
    private long initNum;

    /*
    * 步长
    * */
    private int step;

    /*
    * 是否是有序的ID
    * */
    private int isSeq;

    /*
    * 当前ID所在阶段的开始值
    * */
    private long currentStart;

    /*
    * 当前ID所在阶段的阈值
    * */
    private long nextThreshold;

    /*
    * 业务代码前缀
    * */
    private String idPrefix;

    /*
    * 乐观锁版本号
    * */
    private int version;

    private Date createTime;

    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public long getInitNum() {
        return initNum;
    }

    public void setInitNum(long initNum) {
        this.initNum = initNum;
    }

    public int getStep() {
        return step;
    }

    public void setStep(int step) {
        this.step = step;
    }

    public int getIsSeq() {
        return isSeq;
    }

    public void setIsSeq(int isSeq) {
        this.isSeq = isSeq;
    }

    public long getCurrentStart() {
        return currentStart;
    }

    public void setCurrentStart(long currentStart) {
        this.currentStart = currentStart;
    }

    public long getNextThreshold() {
        return nextThreshold;
    }

    public void setNextThreshold(long nextThreshold) {
        this.nextThreshold = nextThreshold;
    }

    public String getIdPrefix() {
        return idPrefix;
    }

    public void setIdPrefix(String idPrefix) {
        this.idPrefix = idPrefix;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
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

    @Override
    public String toString() {
        return "IdGeneratePO{" +
                "id=" + id +
                ", remark='" + remark + '\'' +
                ", initNum=" + initNum +
                ", step=" + step +
                ", isSeq=" + isSeq +
                ", currentStart=" + currentStart +
                ", nextThreshold=" + nextThreshold +
                ", idPrefix='" + idPrefix + '\'' +
                ", version=" + version +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
