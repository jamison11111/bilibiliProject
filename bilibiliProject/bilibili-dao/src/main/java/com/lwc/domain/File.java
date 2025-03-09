package com.lwc.domain;

import java.util.Date;

/**
 * ClassName: File
 * Description:
 * 文件记录实体类,用于实现文件妙传功能
 * @Author 林伟朝
 * @Create 2024/10/22 9:55
 */
public class File {

    private Long id;

    private String url;

    private String type;

    private String md5;

    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
