package com.lwc.Dao;

import com.lwc.domain.auth.AuthRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * ClassName: AuthRoleMenuDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/15 15:14
 */
@Mapper
public interface AuthRoleMenuDao {

    List<AuthRoleMenu> getRoleMenusByroleIds(@Param("roleIdSet") Set<Long> roleIdSet);
}
