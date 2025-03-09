package com.lwc.Dao;

import com.lwc.domain.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: FollowingGroupDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/11 13:56
 */
@Mapper
public interface FollowingGroupDao {


    FollowingGroup getByType(String type);


    FollowingGroup getById(Long id);

    List<FollowingGroup> getByUserId(Long userId);

    Integer addUserFollwoingGroup(FollowingGroup followingGroup);

    List<FollowingGroup> getUserFollowingGroups(Long userId);
}
