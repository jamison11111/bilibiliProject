package com.lwc.api;

import com.lwc.domain.JsonResponse;
import com.lwc.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * ClassName: FileApi
 * Description:
 * 与文件上传相关的控制层
 * @Author 林伟朝
 * @Create 2024/10/21 21:22
 */
@RestController
public class FileApi {
    @Autowired
    private FileService fileService;



    //在上传文件到服务器之前，会先调用此接口获得上传的文件的二进制流的md5加密字符串,然后到数据库中间检查此文件是否已上传过,从而实现妙传功能
    @PostMapping("/md5files")
    public JsonResponse<String>getFileMD5(MultipartFile file) throws Exception {
        String fileMD5=fileService.getFileMD5(file);
        return new JsonResponse<>(fileMD5);
    }



    /**
     *用户已经在自己的客户端把要上传的文件分成一片片了，分片工作已经做好了，这是分片上传的api
     * @param slice:续点上传的文件分片
     * @param fileMd5:完整文件的md5加密字符串
     * @param sliceNo:当前分片的序号
     * @param totalSliceNo:分片总数
     * @return
     * @throws Exception
     */
    @PutMapping("/file-slices")
    public JsonResponse<String>uploadFileBySlices(MultipartFile slice,
                                                  String fileMd5,
                                                  Integer sliceNo,
                                                  Integer totalSliceNo) throws Exception {

        String filePath=fileService.uploadFileBySlices(slice,fileMd5,sliceNo,totalSliceNo);
        return new JsonResponse<>(filePath);

    }


}
