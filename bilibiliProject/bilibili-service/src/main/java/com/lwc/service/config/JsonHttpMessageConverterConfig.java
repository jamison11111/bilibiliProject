package com.lwc.service.config;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import java.util.Collections;
import java.util.stream.Collectors;
import org.springframework.http.MediaType;


/**
 * ClassName: JsonHttpMessageConverterConfig
 * Description:
 * fastJson依赖相关的配置类
 *
 * @Author 林伟朝
 * @Create 2024/10/9 20:06
 */
@Configuration//配置类,内含component注解
public class JsonHttpMessageConverterConfig {
    @Bean
    @Primary
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();
        FastJsonConfig fastJsonConfig = new FastJsonConfig();


        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");
        fastJsonConfig.setSerializerFeatures(
                SerializerFeature.PrettyFormat,
                SerializerFeature.WriteNullStringAsEmpty,
                SerializerFeature.WriteNullListAsEmpty,
                SerializerFeature.WriteMapNullValue,
                SerializerFeature.MapSortField,//对Map对象先排序再转为json
                SerializerFeature.DisableCircularReferenceDetect);//关闭循环引用检测
        fastConverter.setFastJsonConfig(fastJsonConfig);
        //如果要使用feign进行微服务间的接口调用,则需要加上此配置
        fastConverter.setSupportedMediaTypes(Collections.singletonList(MediaType.APPLICATION_JSON));
        return new HttpMessageConverters(fastConverter);
    }

}
