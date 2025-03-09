package com.lwc.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;

/**
 * ClassName: UserInfo
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/10 10:29
 */
@Document(indexName = "user-infos")
public class UserInfo {
    @Id
    private long id;
    private long userId;
    @Field(type= FieldType.Text)
    private String nick;
    private String avatar;
    private String sign;
    private String gender;
    private String birth;
    @Field(type= FieldType.Date)
    private Date createTime;
    @Field(type= FieldType.Date)
    private Date updateTime;
    private Boolean followed;//冗余字段,若为true，标识本用户既是被关注的对象,也即是说他是被当前用户关注的up主,需设定初始值false

    public Boolean getFollowed() {
        return followed;
    }

    public void setFollowed(Boolean followed) {
        this.followed = followed;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
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
