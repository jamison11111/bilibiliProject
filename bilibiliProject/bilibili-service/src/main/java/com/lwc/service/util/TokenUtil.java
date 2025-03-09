package com.lwc.service.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.lwc.domain.exception.ConditionException;

import java.util.Calendar;
import java.util.Date;

/**
 * ClassName: TokenUtil
 * Description:
 *用于生成JWT-TOKEN令牌的工具类,token本身无需存储在服务端,不仅有利于减轻服务器压力而且还适合于分布式服务集群,
 * 而且token将来是在请求中携带，而不是在cookie里，相对不易拦截，更安全。
 * @Author 林伟朝
 * @Create 2024/10/10 15:10
 */
public class TokenUtil {
    //令牌的签发者，公司名称或者本人名字之类的
    private static final String ISSUER = "lwc";
    //userId形参作为token载荷的一部分,生成accessToken的接口方法
    public static String generateToken(Long userId) throws Exception {
        //指定jwt令牌的加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        Calendar calendar=Calendar.getInstance();
        //设置令牌过期时间为3600s
        calendar.setTime(new Date());
        calendar.add(Calendar.SECOND,3600);
        //生成令牌
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    //封装解析token令牌的Api
    //若解密失败，视异常类型的不同给前端返回不同的信息，提升用户体验
    public static Long verifyAccessToken(String token) {
        try {
            //指定jwt令牌的算法
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
            //生成jwt令牌验证器
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            //解密后的令牌中可以提取出用户id了(当然前提是能加尔米成功的话)
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        } catch (TokenExpiredException e) {
            //后端解析accessToken令牌时,若报了此过期异常码777,前端自动向后端发起刷新令牌请求
            //进行自动获取新accessToken令牌的尝试
            throw new ConditionException("777","accesstoken令牌过期,遇到此777状态码请向后台服务器发起新请求,进行自动刷新accessToken令牌的尝试");
        } catch(Exception e){
            //暂且认为除了过期令牌以外,所有解析异常的令牌都是非法用户令牌
            throw new ConditionException("非法的accesstoken");
        }

    }

    public static String generateRefreshToken(long userId) throws Exception {
        //指定jwt令牌的加密算法
        Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
        Calendar calendar=Calendar.getInstance();
        //设置令牌过期时间为7天
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH,7);
        //生成令牌
        return JWT.create().withKeyId(String.valueOf(userId))
                .withIssuer(ISSUER)
                .withExpiresAt(calendar.getTime())
                .sign(algorithm);
    }

    //只涉及对refreshtoken的解析,不去数据库查refreshtoken的状态
    public static Long verifyrefreshToken(String refreshtoken) {
        try {
            //指定jwt令牌的算法
            Algorithm algorithm = Algorithm.RSA256(RSAUtil.getPublicKey(),RSAUtil.getPrivateKey());
            //生成jwt令牌验证器
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(refreshtoken);
            //解密后的令牌中可以提取出用户id了(当然前提是能解析成功的话)
            String userId = jwt.getKeyId();
            return Long.valueOf(userId);
        } catch (TokenExpiredException e) {
            throw new ConditionException("666","当前处于未登录状态,原因是refreshtoken令牌过期,请重新登录");
        } catch(Exception e){
            throw new ConditionException("非法的refreshtoken");
        }

    }
}
