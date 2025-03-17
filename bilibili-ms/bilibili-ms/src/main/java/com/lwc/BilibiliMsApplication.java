package com.lwc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class BilibiliMsApplication {

    public static void main(String[] args) {
        ApplicationContext app = SpringApplication.run(BilibiliMsApplication.class, args);
    }

}
