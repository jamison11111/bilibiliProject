package com.lwc.service;

import com.lwc.Dao.AuthRoleMenuDao;
import com.lwc.domain.auth.AuthRoleMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * ClassName: AuthRoleMenuService
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/15 14:21
 */
@Service
public class AuthRoleMenuService {
    @Autowired
    private AuthRoleMenuDao authRoleMenuDao;

    public List<AuthRoleMenu> getRoleMenusByroleIds(Set<Long> roleIdSet) {
        return authRoleMenuDao.getRoleMenusByroleIds(roleIdSet);
    }
}
