package com.lwc.Dao;

import com.lwc.domain.auth.AuthRoleElementOperation;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * ClassName: AuthRoleElementOperationDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/15 14:23
 */
@Mapper
public interface AuthRoleElementOperationDao {

    /*@param为传入的集合参数命名，这样就就能在xml文件中用这个参数名来值带这个set进行操作了了*/
    List<AuthRoleElementOperation> getRoleElementOperationByroleIds(@Param("roleIdSet") Set<Long> roleIdSet);
}
