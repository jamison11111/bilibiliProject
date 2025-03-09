package com.lwc.api;

import com.lwc.api.support.UserSupport;
import com.lwc.domain.JsonResponse;
import com.lwc.domain.auth.UserAuthorities;
import com.lwc.service.UserAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: UserAuthApi
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/14 22:34
 */
//与用户权限相关的api,前端传token,后端查出token对应所有的权限返回给前端
@RestController
public class UserAuthApi {
    @Autowired
    private UserSupport userSupport;

    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/user-authorities")
    public JsonResponse<UserAuthorities> getUserAuthorities() {
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities result=userAuthService.getUserAuthorities(userId);
        return new JsonResponse<>(result);

    }
}
