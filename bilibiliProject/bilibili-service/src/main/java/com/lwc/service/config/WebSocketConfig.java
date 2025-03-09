package com.lwc.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * ClassName: WebSocketConfig
 * Description:
 * WebSocket相关的配置类
 * @Author 林伟朝
 * @Create 2024/10/26 14:30
 */
@Configuration
public class WebSocketConfig {

    //该配置类创建一个bean，这个bean是webSocket服务的服务端点导出者对象,负责服务端的webSocket服务的发现和获取
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
