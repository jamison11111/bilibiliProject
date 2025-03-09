package com.lwc.api.aspect;

import com.lwc.api.support.UserSupport;
import com.lwc.domain.annotation.ApiLimitedRole;
import com.lwc.domain.auth.UserRole;
import com.lwc.domain.exception.ConditionException;
import com.lwc.service.UserRoleService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClassName: ApiLimitedRoleAspect
 * Description:
 *根据用户角色的权限限制用户调用api的切面类
 * point cut切点，一般指切入Aop方法的标志性自定义注解;
 * join cut 连接点，对应的被拦截的对象,如被拦截的接口方法等
 * @Author 林伟朝
 * @Create 2024/10/16 10:40
 */
@Order(1)
@Component
@Aspect
public class ApiLimitedRoleAspect {
    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;


    //将切点定义为被自定义的注解修饰的相关方法
    @Pointcut("@annotation(com.lwc.domain.annotation.ApiLimitedRole)")
    public void check(){};//这个check方法存在的意义是?


    //在切入方法check执行之前需要执行的函数逻辑
    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinPoint, ApiLimitedRole apiLimitedRole){
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        //注解中定义的,需要限制接口访问的角色代码列表
        String[] limitedRoleCodeList=apiLimitedRole.limitedRoleCodeList();
        //用set去重
        Set<String> limitedRoleCodeSet= Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        Set<String> userRoleSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        userRoleSet.retainAll(limitedRoleCodeSet);
        if(!userRoleSet.isEmpty()){
            throw new ConditionException("用户所属角色无权访问该接口!");
        }


    }
}
