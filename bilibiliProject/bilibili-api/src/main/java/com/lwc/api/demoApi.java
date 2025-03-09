package com.lwc.api;

import com.lwc.domain.JsonResponse;
import com.lwc.domain.Video;
import com.lwc.service.ElasticSearchService;
import com.lwc.service.demoService;
import com.lwc.service.feign.MsDeclareService;
import com.lwc.service.util.FastDFSUtil;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.print.DocFlavor;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * ClassName: demoAPI
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/8 22:05
 */
@RestController
public class demoApi {
    @Autowired
    private demoService service;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    //操作搜索引擎服务器的相关api
    @Autowired
    private ElasticSearchService elasticSearchService;
    //用于调用另一个微服务的依赖bean的注入
    @Autowired
    private MsDeclareService msDeclareService;

    @GetMapping("/queryTest")
    public String queryTest(Integer age){
        return service.query(age);
    }


    //文件分片的测试接口,临时文件会先存放在客户端本机，或存放在前端服务器
    @GetMapping("/slices")
    public void slices(MultipartFile file)throws Exception{
        fastDFSUtil.convertFileToSlices(file);
    }

    //以模糊查询的方法从es搜索引擎中查询搜索引擎服务器内存放的视频信息，不用token，游客也可以搜
    @GetMapping("/es-videos")
    public JsonResponse<Video> getEsVideos(@RequestParam String keyword){
        Video video=elasticSearchService.getVideo(keyword);
        return new JsonResponse<>(video);
    }

    @GetMapping("/contents")
    public JsonResponse<List<Map<String, Object>>> getContents(@RequestParam String keyword,
                                                               @RequestParam Integer pageNo,
                                                               @RequestParam Integer pageSize) throws IOException {
        List<Map<String,Object>>list=elasticSearchService.getContents(keyword,pageNo,pageSize);
        return new JsonResponse<>(list);
    }

    //删除es搜索引擎服务器内的所有数据
    @DeleteMapping("/es-datas")
    public JsonResponse<String> deleteAll(){
        elasticSearchService.deleteAllVideos();
        elasticSearchService.deleteAllUserInfos();
        return new JsonResponse<>("success");
    }

    /*两个微服务接口的调用的测试*/


    @GetMapping("/msGetDemo")
    public String msDemo(@RequestParam Long id){
        return msDeclareService.msget(id);
    }

    @PostMapping("/msPostDemo")
    public Map<String,Object> msPostDemo(@RequestBody Map<String,Object> params){
        return msDeclareService.mspost(params);
    }

//微服务不可用时,用熔断器进行服务降级,防止整个分布式服务系统崩溃,这是其中一种处理方案
//断路器注解，注明微服务断路时要调用的回调函数和超时时间
    @HystrixCommand(fallbackMethod = "errorMethod",
    commandProperties = {
            @HystrixProperty(
                    name="execution.isolation.thread.timeoutInMilliseconds",
                    value="2000"
            )
    })
    @GetMapping("/breaker-test")
    public String timeout(@RequestParam Long time){
        return msDeclareService.timeout(time);
    }

    public String errorMethod( Long param){
        return "微服务调用超时,触发熔断机制";
    }



}
