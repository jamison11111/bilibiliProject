package com.lwc.api.support;

import com.lwc.domain.RefreshTokenDetail;
import com.lwc.domain.exception.ConditionException;
import com.lwc.service.UserService;
import com.lwc.service.util.TokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ClassName: UserSupport
 * Description:
 *控制器类的相关支持类的包内的类,这是User控制器的支持类
 * @Author 林伟朝
 * @Create 2024/10/10 16:24
 */
@Component
public class UserSupport {

    @Autowired
    private UserService userService;

    //本方法用于解析前端请求头中的令牌,双令牌校验
    public Long getCurrentUserId() {
        //从前端请求头中获取token令牌
        ServletRequestAttributes requestAttributes= (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        String accesstoken=requestAttributes.getRequest().getHeader("accesstoken");
        String refreshtoken=requestAttributes.getRequest().getHeader("refreshtoken");
        //先校验,后查询,校验不通过的话,才查数据库,提升系统性能
        Long userId1= TokenUtil.verifyAccessToken(accesstoken);
        Long userId2= TokenUtil.verifyrefreshToken(refreshtoken);
        if(userId1==null||userId2==null||userId1<0||userId2<0||!userId1.equals(userId2)){
            throw new ConditionException("登录令牌非法,你当前处于未登录状态,无法请求服务器资源");
        }
        //最后再去数据库检查一下refreshToken是否还在
        //进if的话即使accessToken在有效期内,但因为数据库中没有对应的refreshToken,所以相当于还是未登录状态
        RefreshTokenDetail refreshTokenDetail=userService.checkRefreshToken(refreshtoken);
        if(refreshTokenDetail==null){
            throw new ConditionException("用户未登录,无法获取服务器资源");
        }
        return userId1;
    }
}
