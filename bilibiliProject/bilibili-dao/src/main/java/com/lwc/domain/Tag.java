package com.lwc.domain;

import java.util.Date;

/**
 * ClassName: Tag
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/22 15:34
 */
public class Tag {
    private Long id;
    private String name;
    private Date createTime;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
