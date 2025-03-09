package com.lwc.domain;

import java.util.Date;
import java.util.List;

/**
 * ClassName: CollectionGroup
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/23 19:51
 */
public class CollectionGroup {
    private Long id;
    private Long userId;
    private String name;
    private String type;
    private Date createTime;
    private Date updateTime;
    //冗余字段，查询当前用户分组时，冗余字段把每个分组所对应的收藏的视频的信息也顺便传给前端
    private List<VideoCollection>videoCollections;

    public List<VideoCollection> getVideoCollections() {
        return videoCollections;
    }

    public void setVideoCollections(List<VideoCollection> videoCollections) {
        this.videoCollections = videoCollections;
    }

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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
}
