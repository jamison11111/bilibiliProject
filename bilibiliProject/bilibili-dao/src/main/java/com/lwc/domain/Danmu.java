package com.lwc.domain;
import java.util.Date;
/**
 * ClassName: DanMu
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/28 14:09
 */
public class Danmu {
    private Long id;
    private Long userId;
    private Long videoId;
    private String context;
    private String danmuTime;//弹幕在视屏中出现的时间戳位置
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

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getDanmuTime() {
        return danmuTime;
    }

    public void setDanmuTime(String danmuTime) {
        this.danmuTime = danmuTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
