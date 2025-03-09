package com.lwc.service;

import com.lwc.Dao.UserRoleDao;
import com.lwc.domain.auth.AuthRole;
import com.lwc.domain.auth.UserRole;
import com.lwc.domain.constant.AuthRoleConstant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * ClassName: UserRoleService
 * Description:
 *获取用户角色
 * @Author 林伟朝
 * @Create 2024/10/15 13:54
 */

@Service
public class UserRoleService {

    @Autowired
    private UserRoleDao userRoleDao;


    public List<UserRole> getUserRoleByUserId(Long userId) {
        return userRoleDao.getUserRoleByUserId(userId);

    }

    public void addDefaultRole(long id) {
        UserRole userRole = new UserRole();
        userRole.setUserId(id);
        Long roleId = this.getRoleByCode(AuthRoleConstant.ROLE_CODE_Lv0);
        userRole.setRoleId(roleId);
        userRole.setCreateTime(new Date());
        userRoleDao.addDefaultRole(userRole);
    }

    private Long getRoleByCode(String roleCode) {
        return userRoleDao.getRoleByCode(roleCode);
    }
}
