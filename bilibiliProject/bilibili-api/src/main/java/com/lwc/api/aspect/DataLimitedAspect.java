package com.lwc.api.aspect;

import com.lwc.api.support.UserSupport;
import com.lwc.domain.UserMoment;
import com.lwc.domain.annotation.ApiLimitedRole;
import com.lwc.domain.auth.UserRole;
import com.lwc.domain.constant.AuthRoleConstant;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * ClassName: DataLimitedAspect
 * Description:
 *此切面类的功能:角色为Lv1的用户发布动态时只能发布type为1的动态,否则抛出异常
 * point cut切点，一般指切入Aop方法的标志性自定义注解;
 * join cut 连接点，对应的被拦截的对象,如被拦截的接口方法等
 * @Author 林伟朝
 * @Create 2024/10/16 10:40
 */
//此切面类的功能:角色为Lv1的用户发布动态时只能发布type为1的动态,否则抛出异常
@Order(1)
@Component
@Aspect
public class DataLimitedAspect {
    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserRoleService userRoleService;


    //将切点定义为被自定义的注解修饰的相关方法
    @Pointcut("@annotation(com.lwc.domain.annotation.DataLimited)")
    public void check(){};//这个check方法存在的意义是?


    //在切入方法check执行之前需要执行的函数逻辑
    @Before("check()")
    public void doBefore(JoinPoint joinPoint){
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        //获取用户所属的角色
        Set<String> userRoleSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        //获取拦截方法所有参数
        Object[] args = joinPoint.getArgs();
        for (Object arg : args) {
            if (arg instanceof UserMoment) {
                UserMoment userMoment = (UserMoment) arg;
                String type = userMoment.getType();
                //Lv0的用户不能发布类型为1以外的动态,强行发布的话会抛异常
                if(userRoleSet.contains(AuthRoleConstant.ROLE_CODE_Lv0)&&!"0".equals(type)){
                    throw new ConditionException("本用户无权发布此种类型的动态,请检查发布动态的type字段");
                }
            }
        }

    }
}
