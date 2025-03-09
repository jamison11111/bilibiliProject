package com.lwc.domain;

import java.util.Date;

/**
 * ClassName: VideoOperation
 * Description:
 *  用户操作表封装类,用于输入推荐引擎进行算法计算
 * @Author 林伟朝
 * @Create 2024/11/2 10:09
 */
public class VideoOperation {
    private Long id;
    private Long userId;
    private Long videoId;
    private String operationType;
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

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
