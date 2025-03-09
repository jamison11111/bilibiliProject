package com.lwc.Dao;

import com.alibaba.fastjson.JSONObject;
import com.lwc.domain.RefreshTokenDetail;
import com.lwc.domain.User;
import com.lwc.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * ClassName: UserDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/10 10:38
 */
@Mapper//使得该类与xml文件产生关联的mybatis注解
public interface UserDao {




    public User getUserByPhone(String phone);

    Integer addUser(User user);//返回值用于返回注册成功的用户数量

    Integer addUserInfo(UserInfo userInfo);

    User getUserById(Long id);

    UserInfo getUserInfoById(Long userId);

    Integer updateUserInfos(UserInfo userInfo);

    List<UserInfo> getUserInfoByUserIds(Set<Long> followingIdSet);

    //返回满足要求的数据条数,形参Map以多态的形式来接收JsonObject这个接口
    Integer pageCountUserInfos(Map<String,Object> params);

    List<UserInfo> pageListUserInfos(Map<String,Object> params);

    Integer deleteRefreshToken(@Param("userId") Long userId);

    Integer addRefreshToken(@Param("refreshToken") String refreshToken,@Param("userId") Long userId, @Param("createTime") Date createTime);

    RefreshTokenDetail getRefreshTokenDetail(String refreshToken);

    RefreshTokenDetail checkRefreshToken(String refreshToken);

    Integer updateUsers(User user);

    String getSaltByUserId(Long userId);
}
