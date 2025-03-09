package com.lwc.service;

import com.lwc.Dao.UserFollowingDao;
import com.lwc.domain.FollowingGroup;
import com.lwc.domain.User;
import com.lwc.domain.UserFollowing;
import com.lwc.domain.UserInfo;
import com.lwc.domain.constant.UserConstant;
import com.lwc.domain.exception.ConditionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * ClassName: UserFollowingService
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/11 13:56
 */
@Service
public class UserFollowingService {
    @Autowired
    private UserFollowingDao userFollowingDao;

    @Autowired
    private FollwingGroupService followingGroupService;

    @Autowired
    private UserService userService;





    //用户点下关注按钮,前端通过requestBody传参,添加用户关注信息到数据库中的用户关注表
    @Transactional//确保删除和新增用户关注表的操作同时成功,不成功则回滚
    public void addUserFollowing(UserFollowing userFollowing) {
        Long groupId = userFollowing.getGroupId();
        //检查表单信息中是否包含分组Id信息(即用户有无将改up指定到特定的关注分组中去)
        if(groupId == null) {
            //如果关注分组id为空,即表单中没有传这个参数上来，就把这个up分到默认分组去
            //用户关注分组表内默认是有三个分组信息的,下面这行代码仅仅是为了获得默认分组的主键Id而已
            FollowingGroup followingGroup = followingGroupService.getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            //分组表的主键id即为用户关注表的GroupId，通过这个键来关联两张表
            userFollowing.setGroupId(followingGroup.getId());
        }
        else{
            //判断用户指定的关注分组id是否存在
            FollowingGroup follwingGroup = followingGroupService.getById(groupId);
            if(follwingGroup==null){
                throw new ConditionException("用户指定的关注分组不存在!");
            }
        }
        //为up指定关注分组的操作已经完成，做别的逻辑判断
        User usr=userService.getUserById(userFollowing.getUserId());
        if(usr==null){
            throw new ConditionException("关注的用户不存在!");
        }
        //分组指定完成,关注的用户也存在,开始操作数据库写表
        //这个up主可能以前关注过,得先删除以前的表记录,以防数据库中有互相冲突或重复的表记录
        userFollowingDao.deleteUserFollowing(userFollowing.getUserId(),userFollowing.getFollowingId());
        userFollowing.setCreateTime(new Date());
        userFollowingDao.addUserFollowing(userFollowing);



    }


/*本项目中定义的FollowingGroup和UserFollowing对象和数据库中的这两张表并不完全对应相同,这个对象会包含一些数据库表中没有的冗余字段,
这些字段是为了方便本方法进行的多表联查,没有必要在数据表中增添这些冗余字段,占用空间,而且数据库的io操作效率也低,这些操作直接在java层面用业务
逻辑实现即可,又快又省空间,缺点是代码编写难度较大*/
    //获取用户的关注列表,将关注的up按关注分组进行分类
    //三步走
    //第一步:获取关注的up的UserFollowing记录
    //第二步:根据UserFollowing记录获取up主的id，进而查询up主们的详细信息
    //第三步:将up主们的详细信息列表写进对应的分组对象FollowingGroup的属性里
    public List<FollowingGroup> getUserFollowings(Long userId) {
        //获取总的关注列表
        List<UserFollowing>list=userFollowingDao.getUserFollowingByUserId(userId);
        //从关注列表中提取所关注的up主的id的集合
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        //根据up主的id集合获取up主们的详细信息集合
        List<UserInfo> userInfoList=new ArrayList<>();
        if(followingIdSet.size()>0){
            //一个一个去数据库查涉及多次io操作，这是大忌，所以这里专门重新写一个api一次性全部查出来
            userInfoList=userService.getUserInfoByUserIds(followingIdSet);
        }

        //为关注记录userFollowing添加冗余字段,UserInfo,也即该条记录所对应的up主的详细信息
        for(UserFollowing userFollowing:list){
            for(UserInfo userInfo:userInfoList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userFollowing.setUserInfo(userInfo);
                }
            }
        }

        //根据用户id，把用户的所有关注分组查出来(包括默认有的三个分组和自建的分组),也即查询本用户所拥有的分组(分组数量>=3)
        List<FollowingGroup>groupList=followingGroupService.getByUserId(userId);
        //先把全部分组这个分组给展示出来(相当于分组拼接)
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        allGroup.setFollowingUserInfoList(userInfoList);
        //存放所有分组的列表
        List<FollowingGroup>result=new ArrayList<>();
        result.add(allGroup);

        //遍历每个分组,获取每个分组中的所有up主的详细信息,写进列表里
        for(FollowingGroup group:groupList){
            List<UserInfo>infoList=new ArrayList<>();
            for(UserFollowing userFollowing:list){
                if(group.getId().equals(userFollowing.getGroupId())){
                    infoList.add(userFollowing.getUserInfo());
                }
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;
}

//查询当前用户的粉丝列表,互粉的状态要特殊标识
    public List<UserFollowing>getUserFans(Long userId){
        //去数据库查UserFollowing表,查粉本用户的人
        List<UserFollowing>fanList=userFollowingDao.getUserFans(userId);
        //从粉本用户的数据记录中提纯出粉丝们的详细信息,先提取粉丝的Id集合
        Set<Long> fanIdList = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        //根据粉丝Id查询粉丝详细信息
        List<UserInfo>fanInfoList=new ArrayList<>();
        if (!fanIdList.isEmpty()){
            //一次性全查出来，减少数据库的多次io操作
            fanInfoList = userService.getUserInfoByUserIds(fanIdList);
        }
        //根据fanInfoList粉丝信息列表和userId,修改粉丝们的冗余信息字段follow,若为互粉则为true
        for(UserInfo fanInfo: fanInfoList){
            for(UserFollowing fanUserFollowing:fanList){
                if(fanUserFollowing.getUserId().equals(fanInfo.getUserId())){
                    fanInfo.setFollowed(false);//默认状态为非互粉
                    fanUserFollowing.setUserInfo(fanInfo);
                }
            }
            //判断本用户有没有关注这个粉丝
            List<UserFollowing> myUserFollowing = userFollowingDao.getUserFollowingByUserId(userId);
            Set<Long> myIdolID = myUserFollowing.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
            for(Long idolID: myIdolID){
                if(fanInfo.getId()==idolID)
                    fanInfo.setFollowed(true);//若为互粉的粉丝,则标识其状态
            }
        }
        return fanList;//返回粉丝列表，其冗余字段包含这些粉丝的详细信息

    }

    public Long addUserFollowingGroups(FollowingGroup followingGroup) {
        followingGroup.setCreateTime(new Date());
        //用户自建的关注分组新类型,用户自定义分组
        followingGroup.setType(UserConstant.USER_FOLLOWING_GROUP_TYPE_USER);
        followingGroupService.addUserFollowingGroups(followingGroup);
        return followingGroup.getId();

    }

    public List<FollowingGroup> getUserFollowingGroups(Long userId) {
      return followingGroupService.getUserFollowingGroups(userId);
    }

    public void checkFollowingStatus(List<UserInfo> list, Long userId) {
        //查出本用户的关注列表
        List<UserFollowing>userFollowingList=userFollowingDao.getUserFollowingByUserId(userId);
        //将其与当前查出的列表进行比对
        for(UserInfo userInfo:list){
            userInfo.setFollowed(false);
            for(UserFollowing userFollowing:userFollowingList){
                if(userFollowing.getFollowingId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(true);
                }
            }
        }
    }
}
