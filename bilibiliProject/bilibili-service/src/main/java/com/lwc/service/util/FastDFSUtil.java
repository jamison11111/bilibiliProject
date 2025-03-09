package com.lwc.service.util;

import com.github.tobato.fastdfs.domain.fdfs.FileInfo;
import com.github.tobato.fastdfs.domain.fdfs.MetaData;
import com.github.tobato.fastdfs.domain.fdfs.StorePath;
import com.github.tobato.fastdfs.domain.proto.storage.DownloadCallback;
import com.github.tobato.fastdfs.service.AppendFileStorageClient;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.lwc.domain.exception.ConditionException;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.*;

/**
 * ClassName: FastDFSUtil
 * Description:
 *与fastDFS进行交互的一些相关工具类
 * @Author 林伟朝
 * @Create 2024/10/20 17:21
 */
@Component
public class FastDFSUtil {
    //上传普通文件到fastDFS服务器的依赖bean
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    //上传较大文件到fastDFS服务器的依赖bean(内含断点续传相关的api)
    @Autowired
    private AppendFileStorageClient appendFileStorageClient;


    //断点续传api功能需用到redis数据库,故引入相关依赖bean
    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    //获取fdfs服务器资源的http请求前缀
    @Value("${fdfs.http.storage-addr}")
    private String fdfsStorageAddrPrefix;

    //上传文件到storage服务器时,默认上传到的服务器组别为group1
    private static final String DEFAULT_GROUP="group1";

    //断点续传方法中涉及到的三个redis键名静态参数
    private static final String PATH_KEY="path-key:";
    private static final String UPLOADED_SIZE_KEY="uploaded-size-key:";
    private static final String UPLOADED_NO_KEY="uploaded-no-key:";
    //文件分片的默认大小
    private static final int SLICE_SIZE=1024*1024*20;  //20MB



    public String getFileType(MultipartFile file){
        if(file==null){
            throw new ConditionException("文件不存在!");
        }
        String fileName=file.getOriginalFilename();
        int index=fileName.lastIndexOf(".");
        return fileName.substring(index+1);
    }

    //上传,没写上传到哪儿,因为fastDFS会自行决定上传到的存储服务器的位置并返回资源的请求地址，负载均衡，资源被上传到存储服务器的什么位置不是客户端的用户能决定的...
    public String uploadCommonFile(MultipartFile file) throws Exception {
        //存储文件属性的set，比如所有者，创建时间，读写权限等
        Set<MetaData>metaDataSet=new HashSet<>();

        String fileType=this.getFileType(file);

        StorePath storePath=fastFileStorageClient.uploadFile(file.getInputStream(),file.getSize(),fileType,metaDataSet);
        return storePath.getPath();//上传成功后返回其在DFS服务器中的相对路径
    }
    public  void deleteFile(String filePath){
        fastFileStorageClient.deleteFile(filePath);
    }

    //上传可以断点续传的文件，调用这个api的前提是分片工作已经完成，形参已经是分片形式的文件了，并且是第一片
    public String uploadAppenderFile(MultipartFile file)throws Exception{
        String fileType=this.getFileType(file);
        //先上传断点续传文件第一片的内容，并返回上传成功的第一片文件在服务器中的位置信息
        StorePath storePath = appendFileStorageClient.uploadAppenderFile(DEFAULT_GROUP, file.getInputStream(), file.getSize(), fileType);
        return storePath.getPath();
    }

    //在断点续传类型的文件末尾追加内容，开始真正的断点续传工作，调用这个api的前提是分片工作已经完成，形参已经是分片形式的文件了，并且是第一片
    public void modifyAppenderFile(MultipartFile file,String filePath,long offset) throws Exception{
        //需要的传参有:上传到第几组,上传到的路径，上传的文件内容和大小,要上传的file在服务器中已有文件的追加位置(即偏移量))
        appendFileStorageClient.modifyFile(DEFAULT_GROUP,filePath,file.getInputStream(),file.getSize(),offset);
    }



    /**
     * 这是是一个断点续传的完整流程Api
     * @param file,当前文件分片对象
     * @param fileMd5,文件内容进行md5加密后形成的唯一标识符字符串,本api中被作为rediskey的一部分,后续还可被用于秒传功能的开发
     * @param sliceNo,当前上传的分片的序号
     * @param totalSliceNo,整个文件总共包含的分片数
     * @return
     */
    public String uploadFileBySlices(MultipartFile file,String fileMd5,Integer sliceNo,Integer totalSliceNo) throws Exception {
        if(file==null||sliceNo==null||totalSliceNo==null){
            throw new ConditionException("参数异常!");
        }
        //当前文件路径值的完整键名,fileMd5是完整文件经md5加密后的字符串?后续再看看
        String pathKey=PATH_KEY+fileMd5;
        //当前存在于服务器上的(已上传完成的部分)的文件的大小值的键名
        String uploadedSizeKey=UPLOADED_SIZE_KEY+fileMd5;
        //已经上传成功的文件部分的最大片序号的值的键名
        String uploadedNoKey=UPLOADED_NO_KEY+fileMd5;
        //从redis中获取已上传完成的文件大小值，若还没上传，则为空...
        String uploadedSizeStr=redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize=0L;//存储已上传的文件的大小值的变量
        if(!StringUtil.isNullOrEmpty(uploadedSizeStr)){
            uploadedSize=Long.valueOf(uploadedSizeStr);
        }
        if(sliceNo==1){//若上传的是第一个分片
            String path=this.uploadAppenderFile(file);//上传分片1并返回其在服务器中存储的路径
            if(StringUtil.isNullOrEmpty(path)){
                throw new ConditionException("上传失败");
            }
            //能走到这,说明至少第一片是上传成功的,文件在服务器中的路径还是能获得的
            redisTemplate.opsForValue().set(pathKey,path);
            redisTemplate.opsForValue().set(uploadedNoKey,"1");
        }
        //当前在上传的文件分片不是第一片的情况
        else{
            String filePath=redisTemplate.opsForValue().get(pathKey);
            //明明不是第一片,却找不到第一片在服务器上的路径,于是抛异常
            if(StringUtil.isNullOrEmpty(filePath)){
                throw new ConditionException("上传失败");
            }
            //偏移量就是服务器中已有的文件的大小,也就是新分片的追加位置
            this.modifyAppenderFile(file,filePath,uploadedSize);
            redisTemplate.opsForValue().increment(uploadedNoKey);//已上传分片数量+1
        }
        uploadedSize+=file.getSize();//更新已加载的文件大小
        //更新redis中的对应键值
        redisTemplate.opsForValue().set(uploadedSizeKey,String.valueOf(uploadedSize));
        //当前分片上传完成了,判断一下是不是所有的分片都上传完成了
        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
        Integer uploadedNo = Integer.valueOf(uploadedNoStr);
        String resultPath="";//目标路径，如果前端接收到不为空的目标路径,说明所有分片路径都上传完成了
        if(uploadedNo.equals(totalSliceNo)){
            resultPath=redisTemplate.opsForValue().get(pathKey);
            List<String> keyList = Arrays.asList(uploadedNoKey, pathKey, uploadedSizeKey);
            //redis缓存一次性删除列表中所有的键名所对应的键值对
            redisTemplate.delete(keyList);
        }
        return resultPath;
    }


    //上传文件到服务器之前，前端代码会先把文件分片，并存储到客户端的临时目录上，因此传给远端服务器的实际上是一个一个的小分片
    //将客户端的文件先分片,然后再将分片传给服务器，这是分片api
    public void convertFileToSlices(MultipartFile multipartFile) throws IOException {
        //获取文件信息并转为file类型
        String fileType=this.getFileType(multipartFile);
        File file = this.multipartFileToFile(multipartFile);
        long fileLength=file.length();
        int count=1;//分片的计数标识
        for(int i=0;i<fileLength;i+=SLICE_SIZE){
            RandomAccessFile randomAccessFile=new RandomAccessFile(file,"r");
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            //将随机读取器的内容读进byte数组
            int len = randomAccessFile.read(bytes);
            //设置临时分片文件在客户端的存放位置
            String path="F:/"+count+"."+fileType;
            //承载分片的文件对象
            File slice=new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes,0,len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        //分片完成，删除临时文件
        file.delete();
    }



    public File multipartFileToFile(MultipartFile multipartFile) throws IOException {
        String originalFileName=multipartFile.getOriginalFilename();
        String[] fileName=originalFileName.split("\\.");
        //创建一个临时文件用于承接multipartFile
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        multipartFile.transferTo(file);
        return file;

    }


    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String path) throws Exception {
        //获取服务器资源的相关信息
        FileInfo fileInfo = fastFileStorageClient.queryFileInfo(DEFAULT_GROUP, path);
        long totalFileSize = fileInfo.getFileSize();
        //包装出完整的http资源请求路径
        String url=fdfsStorageAddrPrefix+path;
        //获取请求头名称的枚举类泛型
        Enumeration<String> headerNames = request.getHeaderNames();
        Map<String,Object>headers=new HashMap<>();
        while(headerNames.hasMoreElements()){
            String header = headerNames.nextElement();
            headers.put(header,request.getHeader(header));
        }
        //将客户端请求转发给DFS服务器（DFS可能需要验证用户信息，还需要知道请求第几片等信息，这些都在请求头中），所以需要获取请求头信息
        //若请求头中包含了当前求取的分片的大小和位置信息，故需要提取出来，否则就塞给它默认值
        String rangeStr = request.getHeader("Range");
        String[] range;
        if(StringUtil.isNullOrEmpty(rangeStr)){
            rangeStr="bytes=0-"+(totalFileSize-1);
        }
        range=rangeStr.split("bytes=|-");
        long begin=0;
        //长度至少为2,说明请求头中含分片的起始位置
        if(range.length>=2){
            begin=Long.parseLong(range[1]);
        }
        long end=totalFileSize-1;
        //长度至少为3，说明请求头中也包含分片的结束位置
        if(range.length>=3){
            end=Long.parseLong(range[2]);
        }
        long len=end-begin+1;

        /*为DFS后端提前写好响应体的一些信息，这样DFS后台就不用费劲的写响应体了
         ,它只用负责根据请求的路径和请求的片的位置返回相应的输出流给response就行了*/
        String contentRange="bytes " + begin + "-" + end+"/"+totalFileSize;
        response.setHeader("Content-Range",contentRange);
        response.setHeader("Accept-Ranges","bytes");
        response.setHeader("Content-Type","video/mp4");
        response.setContentLength((int)len);
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
        //转发完整的请求给DFS服务器，并贴心的提前为DFS做好一些响应头的设置
        HttpUtil.get(url,headers,response);

    }

    //字符串正则表达式分割用法示例
    public static void main(String[] args) {

        String testStr="byte=123-999999";
        String[] split = testStr.split("yte=|-");
        System.out.println(split.length);
    }


    public void downLoadFile(String url, String localPath) {
        fastFileStorageClient.downloadFile(DEFAULT_GROUP, url,
                new DownloadCallback<String>() {
            /*此回调函数的形参输入流包含下载下来的文件内容,输入流将其写进内存数组buffer中,
            输出流将内容写进file对象中*/
                    @Override
                    public String recv(InputStream ins) throws IOException {
                        File file = new File(localPath);
                        OutputStream os = new FileOutputStream(file);
                        int len = 0;
                        byte[] buffer = new byte[1024];
                        while ((len = ins.read(buffer)) != -1) {
                            os.write(buffer, 0, len);
                        }
                        os.close();
                        ins.close();
                        return "success";
                    }
                });
    }
}
