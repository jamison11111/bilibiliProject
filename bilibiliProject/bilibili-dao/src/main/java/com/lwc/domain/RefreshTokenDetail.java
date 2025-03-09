package com.lwc.domain;

import java.util.Date;

/**
 * ClassName: RefreshTokenDetail
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/16 16:25
 */
public class RefreshTokenDetail {
    private Long id;
    private Long userId;
    private String refreshToken;
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

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
