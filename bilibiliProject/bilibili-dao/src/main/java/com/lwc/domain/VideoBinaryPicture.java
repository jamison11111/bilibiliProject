package com.lwc.domain;

import java.util.Date;

/**
 * ClassName: VideoBinaryPicture
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/11/4 19:14
 */
public class VideoBinaryPicture {
    private Long id;
    private Long videoId;
    private Integer frameNo;
    private String url;
    private Long videoTimeStamp;
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

    public Integer getFrameNo() {
        return frameNo;
    }

    public void setFrameNo(Integer frameNo) {
        this.frameNo = frameNo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getVideoTimeStamp() {
        return videoTimeStamp;
    }

    public void setVideoTimeStamp(Long videoTimeStamp) {
        this.videoTimeStamp = videoTimeStamp;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
