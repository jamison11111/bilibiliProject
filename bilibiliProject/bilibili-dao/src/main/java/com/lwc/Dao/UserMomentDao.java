package com.lwc.Dao;

import com.lwc.domain.UserMoment;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: UserMomentsDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/14 10:14
 */
@Mapper
public interface UserMomentDao {
    Integer addUserMoments(UserMoment userMoment);

}
