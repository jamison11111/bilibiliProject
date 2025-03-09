package com.lwc.domain;

import java.util.Date;
import java.util.List;

/**
 * ClassName: VideoComment
 * Description:
 * 邻接表数据结构涉及的评论表，优点是只用一张表，节省存储空间,缺点是当评论层级大于两层时查询起来逻辑复杂
 * @Author 林伟朝
 * @Create 2024/10/24 16:47
 */
public class VideoComment {
    private Long id;
    private Long videoId;//二级评论的该字段为空
    private Long userId;//创建本条评论的用户的id
    private String comment;
    private Long replyUserId;//回复本条评论的用户的id(更新时用)
    private Long rootId;//根结点评论id,当评论为二级评论或者是没有二级评论的一级评论时,该字段为空
    private Date createTime;
    private Date updateTime;
    /*存放二级评论的列表,若本评论本身就为二级评论或本一级评论暂无人回复,则此字段为空*/
    private List<VideoComment> childList;
    private UserInfo userInfo;
    private UserInfo replyUserInfo;

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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getReplyUserId() {
        return replyUserId;
    }

    public void setReplyUserId(Long replyUserId) {
        this.replyUserId = replyUserId;
    }

    public Long getRootId() {
        return rootId;
    }

    public void setRootId(Long rootId) {
        this.rootId = rootId;
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

    public List<VideoComment> getChildList() {
        return childList;
    }

    public void setChildList(List<VideoComment> childList) {
        this.childList = childList;
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    public UserInfo getReplyUserInfo() {
        return replyUserInfo;
    }

    public void setReplyUserInfo(UserInfo replyUserInfo) {
        this.replyUserInfo = replyUserInfo;
    }
}
