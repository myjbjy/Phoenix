package com.myjbjy.pojo;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author myj
 */
public class MqLocalMsgRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 目标交换机
     */
    private String targetExchange;

    /**
     * 消息路由
     */
    private String routingKey;

    /**
     * 消息内容
     */
    private String msgContent;

    private LocalDateTime createdTime;

    private LocalDateTime updatedTime;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTargetExchange() {
        return targetExchange;
    }

    public void setTargetExchange(String targetExchange) {
        this.targetExchange = targetExchange;
    }

    public String getRoutingKey() {
        return routingKey;
    }

    public void setRoutingKey(String routingKey) {
        this.routingKey = routingKey;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }

    @Override
    public String toString() {
        return "MqLocalMsgRecord{" +
        "id=" + id +
        ", targetExchange=" + targetExchange +
        ", routingKey=" + routingKey +
        ", msgContent=" + msgContent +
        ", createdTime=" + createdTime +
        ", updatedTime=" + updatedTime +
        "}";
    }
}
