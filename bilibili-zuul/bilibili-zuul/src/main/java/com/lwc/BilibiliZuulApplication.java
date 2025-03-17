package com.lwc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableZuulProxy //开启zuul网关功能
public class BilibiliZuulApplication {

    public static void main(String[] args) {

        ApplicationContext app=SpringApplication.run(BilibiliZuulApplication.class, args);
    }

}
