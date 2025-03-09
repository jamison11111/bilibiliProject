package com.lwc.api;

import com.lwc.api.support.UserSupport;
import com.lwc.domain.JsonResponse;
import com.lwc.domain.User;
import com.lwc.domain.UserInfo;
import com.lwc.service.FollwingGroupService;
import com.lwc.service.UserFollowingService;
import com.lwc.service.UserService;
import com.lwc.service.util.RSAUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.StreamingHttpOutputMessage;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName: UserApi
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/10 10:38
 */
@SuppressWarnings("LanguageDetectionInspection")
@RestController
public class UserApi {

    @Autowired
    private UserService userService;

    @Autowired
    private UserFollowingService userFollowingService;

    @Autowired
    private FollwingGroupService follwingGroupService;

    //本控制器的辅助类Bean
    @Autowired
    private UserSupport userSupport;



    /*本接口路径是rsa-publickeys的缩写,前端若接收到一些用户的敏感信息，则会向后端服务器调用此接口请求一个RSA算法公钥,先对信息进行加密
然后再传输给后端,后端通过后端服务器的私钥可以解密,这保证了信息传输过程中的安全不泄露*/
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey() {
        String pk = RSAUtil.getPublicKeyStr();//从后端服务器上的加密工具类调用公钥获取接口
        return new JsonResponse<>(pk);
    }

    //post请求注册用户的接口,post非幂等操作,多次操作会产生不同的影响,接口路径是资源存放的路径而不是资源本身
    @PostMapping("/user")
    public JsonResponse<String> addUser(@RequestBody User user) {//此注解将前端表单传来的json数据作为响应体封装为对象传给User
        userService.addUser(user);
        return JsonResponse.success();
    }


    //用户登录接口,前端提交用户信息发到后台,后台去数据库校验,若校验成功,向前端返回用户登录令牌
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token=userService.login(user);
        return JsonResponse.success(token);
    }


    //前端带着token请求头到后台来请求获取用户信息时，调用此接口
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo() {
        Long userId = userSupport.getCurrentUserId();
        //走到这里不报异常，说明请求头token解析成功,用户存在且合法，令牌也没过期
        User user=userService.getUserById(userId);
        return new JsonResponse<>(user);

    }
    //修改用户信息
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo) {
        /*关于为何要用通过解密请求头中的令牌来获取用户id,出于以下几点考虑
        一:对用户信息进行相关操作,必须是登录了的用户才能进行,令牌有登录鉴权作用,并且还有有效期
        能防止没登陆权限的人任意修改用户信息。
        二:用户id直接放在请求体中不安全容易被截取,请求头中的token比较难截取,就算被截取也很难解密得到令牌中的用户信息
        虽然黑客仍能使用截取到的token进行登录鉴权,但令牌有有效期,而且想直接解密令牌获得里面的用户信息很难。
        总之就是安全,一般不会把id直接从前端传到后端,都是从令牌中获取的用户id*/
        //令牌解析失败,相当于没登陆,直接抛出异常，接口内的操作根本无法执行
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserInfos(userInfo);
        System.out.println("更新成功");
        return JsonResponse.success();
    }

    //doubleTokens双令牌登录方法,好处之一是可以自动刷新access-token,登录状态不会过期
    //好处之二是,用户退出登录后,数据库中的refreshToken会被删掉,access-token即使每过期
    //也无法继续操作了,因为双令牌少了一个令牌就根本无法登录,refresh令牌根本就没发布,用户数据安全性提升
    @PostMapping("/double-tokens")
    public JsonResponse<Map<String,Object>>loginForDts(@RequestBody User user) throws Exception {
        //返回两个token给前端,用map封装
        Map<String,Object> map=userService.loginForDts(user);
        return new JsonResponse<>(map);
    }

    //退出登录时,需删除数据库中对应的的refreshToken
    @DeleteMapping("/refresh-tokens")
    public JsonResponse<String> logout(HttpServletRequest request) {
        //在登录状态下,才有删除令牌之说,都不处于登录状态,何来登出之说,所以获取id必须写在开头
        Long userId = userSupport.getCurrentUserId();
        userService.logout(userId);
        return JsonResponse.success();
    }

    //accessToken过期时,前端收到令牌过期状态码后自动发送请求到该接口,
    // 去数据库查询refreshToken状态,向服务器请求发放一个新accessToken
    @PostMapping("/access-tokens")
    public JsonResponse<String>refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken");
        //查询refreshToken状态,没过期则发布新的accessToken给前端
        String accessToken = userService.refreshAccessToken(refreshToken);
        return JsonResponse.success(accessToken);
    }

    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) throws Exception{
        //校验登录,不抛异常说明处于登录状态,且数据库中存在该用户
        Long userId = userSupport.getCurrentUserId();
        userService.updateUsers(user,userId);
        return JsonResponse.success();

    }







}




