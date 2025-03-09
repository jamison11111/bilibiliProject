package com.lwc.domain.auth;

import java.util.Date;

/**
 * ClassName: AuthRoleElementOperation
 * Description:
 *角色与页面元素操作关联表
 * @Author 林伟朝
 * @Create 2024/10/14 22:06
 */
public class AuthRoleElementOperation {
    private Long id;
    private Long roleId;
    private Long elementOperationId;
    private Date createTime;
    //冗余字段，用于联表查询,减少对数据库的查询次数,提高查询效率,后续用到时自行体会
    private AuthElementOperation authElementOperation;



    public AuthElementOperation getAuthElementOperation() {
        return authElementOperation;
    }

    public void setAuthElementOperation(AuthElementOperation authElementOperation) {
        this.authElementOperation = authElementOperation;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    public Long getElementOperationId() {
        return elementOperationId;
    }

    public void setElementOperationId(Long elementOperationId) {
        this.elementOperationId = elementOperationId;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
