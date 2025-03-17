package com.lwc.ms.api;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * ClassName: DemoApi
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/11/6 20:33
 */
@RestController
public class DemoApi {

    @GetMapping("/demos")
    public String msget(@RequestParam Long id){
        return "微服务get接口调用成功";
    }

    @PostMapping("/demos")
    public Map<String, Object> mspost(@RequestBody Map<String,Object>params){
        return params;
    }

    @GetMapping("/timeout")
    public String timeout(@RequestParam Long time){
        try {
            Thread.sleep(time);
        }catch (InterruptedException e){
            e.printStackTrace();
        }
        return "微服务断路器熔断测试,返回此字段说明微服务调用未超时,断路器未被触发";
    }


}
