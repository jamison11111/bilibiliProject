package com.lwc.service.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.filter.RequestContextFilter;

import java.util.Map;

/**
 * ClassName: MsDeclareService
 * Description:
 *微服务接口声明包,其内是一些微服务接口的声明
 * @Author 林伟朝
 * @Create 2024/11/6 20:53
 */

//本接口类要调用的微服务名称
@FeignClient("lwc-micro-service")
public interface MsDeclareService {

    /*这个微服务内的接口们的声明*/
    @GetMapping("/demos")
    public String msget(@RequestParam Long id);

    @PostMapping("/demos")
    public Map<String,Object> mspost(@RequestBody Map<String,Object> params);

    @GetMapping("/timeout")
    public String timeout(@RequestParam Long time);

}
