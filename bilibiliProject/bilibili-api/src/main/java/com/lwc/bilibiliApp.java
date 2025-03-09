package com.lwc;

import com.lwc.service.websocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.hystrix.EnableHystrix;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * ClassName: blibliApp
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/8 20:36
 */
//springboot启动类,也是本项目的入口

@SpringBootApplication
@EnableTransactionManagement//开启项目的事务管理器
@EnableAsync//允许项目异步调由Async注解的方法
@EnableScheduling//允许项目运行包含定时任务注解的方法
@EnableFeignClients(basePackages = "com.lwc.service.feign")//为本项目使能微服务调用的客户端依赖并指定微服务接口声明包所在的位置
@EnableHystrix//使能项目中的Hystrix断路器
public class bilibiliApp {
    public static void main(String[] args) {
        ApplicationContext app=SpringApplication.run(bilibiliApp.class, args);
        //WebSocketService是多例模式下的bean类，其内的
        WebSocketService.setApplicationContext(app);

    }
}
