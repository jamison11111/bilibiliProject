package com.lwc.domain;

import java.util.Date;

/**
 * ClassName: VideoView
 * Description:
 * 视频观看记录表
 * @Author 林伟朝
 * @Create 2024/11/1 21:14
 */
public class VideoView {

    private Long id;
    private Long videoId;
    private Long userId;
    private String clientId;//客户端id，由请求中解析出的客户浏览器和操作系统决定
    private String ip;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

}
