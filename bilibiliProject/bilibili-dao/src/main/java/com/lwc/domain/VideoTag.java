package com.lwc.domain;

import java.util.Date;

/**
 * ClassName: VideoTag
 * Description:
 * 视频标签关联表的封装类对象
 * @Author 林伟朝
 * @Create 2024/10/22 15:25
 */
public class VideoTag {
    private Long id;
    private Long videoId;
    private Long tagId;
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

    public Long getTagId() {
        return tagId;
    }

    public void setTagId(Long tagId) {
        this.tagId = tagId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
