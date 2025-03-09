package com.lwc.service;

import com.lwc.Dao.AuthRoleElementOperationDao;
import com.lwc.domain.auth.AuthRoleElementOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * ClassName: AuthRoleElementOperationService
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/15 14:21
 */
@Service
public class AuthRoleElementOperationService {
    @Autowired
    private AuthRoleElementOperationDao authRoleElementOperationDao;

    public List<AuthRoleElementOperation> getRoleElementOperationByroleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationDao.getRoleElementOperationByroleIds(roleIdSet);
        
    }
}
