package com.lwc.service;

import com.lwc.Dao.daoDemo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * ClassName: demoService
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/8 22:00
 */
@Service
public class demoService {
    @Autowired
    private daoDemo demoDao;

    //业务层调用数据层查询数据库数据的demo
    public String query(Integer age){
        return demoDao.query(age);
    }

}
