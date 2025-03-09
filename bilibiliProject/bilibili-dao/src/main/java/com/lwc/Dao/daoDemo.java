package com.lwc.Dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: daoDemo
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/8 21:35
 */


@Mapper//注册进springboot项目容器中
public interface daoDemo {
    public String query(Integer age);
}
