package com.lwc.domain.annotation;

import org.springframework.stereotype.Component;

import javax.print.DocFlavor;
import java.lang.annotation.*;

/**
 * ClassName: ApiLimitedRole
 * Description:
 *自定义注解，标识在方法上，在JVM运行时仍保留
 * @Author 林伟朝
 * @Create 2024/10/16 10:15
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Documented
@Component
public @interface ApiLimitedRole {

    //限制角色的编码列表
    String[] limitedRoleCodeList() default {};
}
