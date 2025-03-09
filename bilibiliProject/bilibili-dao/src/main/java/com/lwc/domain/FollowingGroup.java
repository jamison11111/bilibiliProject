package com.lwc.domain;

import java.util.Date;
import java.util.List;

/**
 * ClassName: FollowingGroup
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/11 13:54
 */
public class FollowingGroup {
    private Long id;
    private Long userId;
    private String name;
    private String type;

    public List<UserInfo> getFollowingUserInfoList() {
        return followingUserInfoList;
    }

    public void setFollowingUserInfoList(List<UserInfo> followingUserInfoList) {
        this.followingUserInfoList = followingUserInfoList;
    }

    //关注者的详细信息的列表
    private List<UserInfo> followingUserInfoList;

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
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

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    private Date createTime;
    private Date updateTime;
}
