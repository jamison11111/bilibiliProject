package com.lwc.service;

import com.lwc.Dao.FollowingGroupDao;
import com.lwc.domain.FollowingGroup;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * ClassName: FollwingGroupService
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/11 13:57
 */
@Service
public class FollwingGroupService {
    @Autowired
    private FollowingGroupDao followingGroupDao;


    //根据关注分组获得查询对应的关注分组的信息
    public FollowingGroup getByType(String type){
        return followingGroupDao.getByType(type);
    }

    //根据分组id查询对应的关注分组的信息
    public FollowingGroup getById(Long id){
        return followingGroupDao.getById(id);
    }

    public List<FollowingGroup> getByUserId(Long userId) {
        return followingGroupDao.getByUserId(userId);
    }

    public void addUserFollowingGroups(FollowingGroup followingGroup) {
          followingGroupDao.addUserFollwoingGroup(followingGroup);

    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
        return followingGroupDao.getUserFollowingGroups(userId);
    }
}
