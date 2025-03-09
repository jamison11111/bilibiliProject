package com.lwc.Dao;

import com.lwc.domain.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * ClassName: UserFollowingDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/11 13:55
 */
@Mapper
public interface UserFollowingDao {
    Integer deleteUserFollowing(@Param("userId") Long userId,@Param("followingId") Long followingId);

    Integer addUserFollowing(UserFollowing userFollowing);

    List<UserFollowing> getUserFollowingByUserId(Long userId);

    List<UserFollowing> getUserFans(Long followingId);
}
