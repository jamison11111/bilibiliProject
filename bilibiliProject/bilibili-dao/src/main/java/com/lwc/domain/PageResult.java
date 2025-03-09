package com.lwc.domain;

import java.util.List;

/**
 * ClassName: PageResult
 * Description:
 *分页查询结果的封装类,包含两个字段，页面数据list和满足要求的总的数据条数total
 * @Author 林伟朝
 * @Create 2024/10/12 11:07
 */
public class PageResult<T> {
    private Integer total;//总的数据条数

    private List<T> list;

    public PageResult(Integer total, List<T> list) {
        this.total = total;
        this.list = list;
    }

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(List<T> list) {
        this.list = list;
    }
}
