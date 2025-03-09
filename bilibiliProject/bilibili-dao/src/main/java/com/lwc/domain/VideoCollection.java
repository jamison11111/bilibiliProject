package com.lwc.domain;

import java.util.Date;

/**
 * ClassName: VideoCollection
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/23 19:49
 */
public class VideoCollection {
    private Long id;
    private Long videoId;
    private Long userId;
    private Long groupId;
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

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
