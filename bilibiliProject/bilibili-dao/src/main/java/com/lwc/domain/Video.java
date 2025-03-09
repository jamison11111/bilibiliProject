package com.lwc.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Date;
import java.util.List;

/**
 * ClassName: Video
 * Description:
 * elasticSearch以文档的形式存储java类对象,需要在java端为这些文档信息做好注解映射标注
 * @Author 林伟朝
 * @Create 2024/10/22 15:21
 */
//标注这个类对象的信息在搜索引擎中对应的标签，提高搜索效率
@Document(indexName = "videos")
public class Video {

    //标识该字段在ES中以主键id的方式进行存储
    @Id
    private Long id;

    //标注该字段在搜索引擎内以Long类型存储
    @Field(type= FieldType.Long)
    private Long userId;

    //不做注解的字段信息不会存入搜索引擎内部
    private String url;//视频链接
    private String thumbnail;//封面链接

    //标识其为ES引擎中需要进行分词处理和搜索操作的字段
    @Field(type= FieldType.Text)
    private String title;


    private String type;
    private String duration;
    private String area;

    //标识其为ES引擎中需要进行分词处理和搜索操作的字段
    @Field(type= FieldType.Text)
    private String description;//视频简介

    @Field(type= FieldType.Date)
    private Date createTime;

    @Field(type= FieldType.Date)
    private Date updateTime;


    private List<VideoTag>videoTagList;//冗余字段,视频的标签列表

    public String getDescription() {
        return description;
    }


    public void setDescription(String description) {
        this.description = description;
    }

    public List<VideoTag> getVideoTagList() {
        return videoTagList;
    }

    public void setVideoTagList(List<VideoTag> videoTagList) {
        this.videoTagList = videoTagList;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
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
