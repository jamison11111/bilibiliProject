package com.lwc.Dao;

import com.lwc.domain.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * ClassName: UserRoleDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/15 14:00
 */
@Mapper
public interface UserRoleDao {

    List<UserRole> getUserRoleByUserId(Long userId);


    Integer addDefaultRole(UserRole userRole);

    Long getRoleByCode(String roleCode);
}
