package org.golive.user.provider.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/*
* 生产者配置信息
* */
@ConfigurationProperties(prefix = "golive.rmq.producer")
@Configuration
public class RocketMQProducerProperties {

    private String nameSrv;  // RocketMQ的nameServer服务器地址
    private String groupName;  // 分组名称
    private int retryTimes;  // 消息发失败重试次数
    private int sendTimeOut;  // 超时时间

    @Override
    public String toString() {
        return "RocketMQProducerProperties{" +
                "nameSrv='" + nameSrv + '\'' +
                ", groupName='" + groupName + '\'' +
                ", retryTimes=" + retryTimes +
                ", sendTimeout=" + sendTimeOut +
                '}';
    }

    public String getNameSrv() {
        return nameSrv;
    }

    public void setNameSrv(String nameSrv) {
        this.nameSrv = nameSrv;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getRetryTimes() {
        return retryTimes;
    }

    public void setRetryTimes(int retryTimes) {
        this.retryTimes = retryTimes;
    }

    public int getSendTimeout() {
        return sendTimeOut;
    }

    public void setSendTimeout(int sendTimeout) {
        this.sendTimeOut = sendTimeout;
    }
}
