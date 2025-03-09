package com.lwc.api;

import com.alibaba.fastjson.JSONObject;
import com.lwc.api.support.UserSupport;
import com.lwc.domain.*;
import com.lwc.service.UserFollowingService;
import com.lwc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ClassName: UserFollowingApi
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/11 20:54
 */
@RestController
public class UserFollowingApi {
    @Autowired
    UserFollowingService userFollowingService;
    @Autowired
    UserSupport userSupport;
    @Autowired
    private UserService userService;

    //增加关注
    @PostMapping("/user-followings")
    public JsonResponse<String> addUserFollowing(@RequestBody UserFollowing userFollowing) {
        Long UserId = userSupport.getCurrentUserId();
        //从辅助bean获取当前用户id
        userFollowing.setUserId(UserId);
        userFollowingService.addUserFollowing(userFollowing);
        return JsonResponse.success();
    }

    //查询用户关注列表，以分组形式返回
    @GetMapping("/user-followings")
    public JsonResponse<List<FollowingGroup>> getUserFollowings(){
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup> result = userFollowingService.getUserFollowings(userId);
        return new JsonResponse<>(result);
    }

    //查询粉丝列表,以userMapping形式返回
    @GetMapping("/user-fans")
    public JsonResponse<List<UserFollowing>> getUserFans() {
        Long userId = userSupport.getCurrentUserId();
        List<UserFollowing> result = userFollowingService.getUserFans(userId);
        return new JsonResponse<>(result);
    }

    //新建用户关注分组,并把新建的分组的id返回给前端
    @PostMapping("/user-following-groups")
    public JsonResponse<Long>addUserFollowingGroups(@RequestBody FollowingGroup followingGroup) {
        Long userId = userSupport.getCurrentUserId();
        followingGroup.setUserId(userId);
        Long groupId=userFollowingService.addUserFollowingGroups(followingGroup);
        return new JsonResponse<>(groupId);
    }
    //查询用户关注分组,只返回分组本身，而不用返回分组内部的up的详细详细信息
    @GetMapping("/user-following-groups")
    public JsonResponse<List<FollowingGroup>> getUserFollowingGroups() {
        Long userId = userSupport.getCurrentUserId();
        List<FollowingGroup>list=userFollowingService.getUserFollowingGroups(userId);
        return new JsonResponse<>(list);
    }

    //用户分页查询接口,分页查询一些up以供关注，且主持根据昵称nick的模糊查询
    //传参告知要查看第几页，每页多大，是否模糊查询,是哪个用户在查,然后在服务层编写查询逻辑或直接调用专业api完成功能即可
    @GetMapping("/user-infos")//有RequestParam注解的输入参数必须传参
    public JsonResponse<PageResult<UserInfo>> pageListUserInfos(@RequestParam Integer no, @RequestParam Integer size, String nick) {
        Long userId = userSupport.getCurrentUserId();
        //JsonObject是fastJson包内的一个类,和Map差不多用法,但比Map更只能和方便
        JSONObject params=new JSONObject();
        params.put("no", no);
        params.put("size", size);
        params.put("nick", nick);
        params.put("userId", userId);
        PageResult<UserInfo>result=userService.pageListUserInfos(params);
        //新增功能,检查查询出来的用户有没有本用户关注过的用户,有的话做标记
        if(result.getTotal()>0){
            //检查当前页的List中用户信息,若关注过,则修改标记followed为已关注
            userFollowingService.checkFollowingStatus(result.getList(),userId);
        }
        return new JsonResponse<>(result);

    }


}
