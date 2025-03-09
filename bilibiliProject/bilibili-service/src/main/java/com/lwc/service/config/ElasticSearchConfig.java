package com.lwc.service.config;

import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;

/**
 * ClassName: ElasticSearchConfig
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/31 11:07
 */
@Configuration
public class ElasticSearchConfig extends AbstractElasticsearchConfiguration {

    //将配置文件的地址和端口注入属性字段
    @Value("${elasticsearch.url}")
    private String elasticsearchUrl;


    //生成一个可与es引擎交互的bean类供本项目使用
    @Bean
    @Override
    public RestHighLevelClient elasticsearchClient() {
        final ClientConfiguration clientConfiguration = ClientConfiguration.builder().
                                                        connectedTo(elasticsearchUrl).
                                                        build();
        return RestClients.create(clientConfiguration).rest();


    }

}
