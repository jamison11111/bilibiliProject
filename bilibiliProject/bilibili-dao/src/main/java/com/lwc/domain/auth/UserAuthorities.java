package com.lwc.domain.auth;

import java.util.Date;
import java.util.List;


/**
 * ClassName: UserAuthorities
 * Description:
 *用户权限类,封装了用户的所有权限信息
 * @Author 林伟朝
 * @Create 2024/10/14 21:55
 */
public class UserAuthorities {
    List<AuthRoleElementOperation>roleElementOperationList;

    List<AuthRoleMenu> roleMenuList;

    public List<AuthRoleElementOperation> getRoleElementOperationList() {
        return roleElementOperationList;
    }

    public void setRoleElementOperationList(List<AuthRoleElementOperation> roleElementOperationList) {
        this.roleElementOperationList = roleElementOperationList;
    }

    public List<AuthRoleMenu> getRoleMenuList() {
        return roleMenuList;
    }

    public void setRoleMenuList(List<AuthRoleMenu> roleMenuList) {
        this.roleMenuList = roleMenuList;
    }
}
