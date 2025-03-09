package com.lwc.domain;

import java.util.Date;

/**
 * ClassName: UserPreference
 * Description:
 *存储用户对某个视频的偏好得分
 * @Author 林伟朝
 * @Create 2024/11/2 10:14
 */
public class UserPreference {
    private Long id;
    private Long userId;
    private Long videoId;
    private Float value;//得分字段，通过sql语句计算得出值
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getVideoId() {
        return videoId;
    }

    public void setVideoId(Long videoId) {
        this.videoId = videoId;
    }

    public Float getValue() {
        return value;
    }

    public void setValue(Float value) {
        this.value = value;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
