package com.lwc.service;

import com.lwc.domain.auth.AuthRoleElementOperation;
import com.lwc.domain.auth.AuthRoleMenu;
import com.lwc.domain.auth.UserAuthorities;
import com.lwc.domain.auth.UserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClassName: UserAuthService
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/14 22:38
 */
@Service
public class UserAuthService {
    //获取用户角色的bean
    @Autowired
    private UserRoleService userRoleService;


    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;


    @Autowired
    private AuthRoleMenuService authRoleMenuService;


    public UserAuthorities getUserAuthorities(Long userId) {
        //根据用户id获得用户角色关联表（相当于获取了用户的角色id，也即获取了用户的角色）
        //都是通过查关联的中间表的形式来查到相应的权限或角色的
        List<UserRole> userRoleList=userRoleService.getUserRoleByUserId(userId);
        //从关联表中获取所有的角色id，即所有的角色
        Set<Long>roleIdSet=userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());

        //查与角色们关联的另外两张关联表
        List<AuthRoleElementOperation>authRoleElementOperations=authRoleElementOperationService.getRoleElementOperationByroleIds(roleIdSet);
        List<AuthRoleMenu>authRoleMenus=authRoleMenuService.getRoleMenusByroleIds(roleIdSet);

        UserAuthorities userAuthorities=new UserAuthorities();
        userAuthorities.setRoleElementOperationList(authRoleElementOperations);
        userAuthorities.setRoleMenuList(authRoleMenus);
        return userAuthorities;



    }

    public void addDefaultRole(long id) {
        userRoleService.addDefaultRole(id);
    }
}
