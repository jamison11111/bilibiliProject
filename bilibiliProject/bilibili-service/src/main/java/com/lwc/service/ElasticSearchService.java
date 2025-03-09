package com.lwc.service;

import com.lwc.Dao.repository.UserInfoRepository;
import com.lwc.Dao.repository.VideoRepository;
import com.lwc.domain.UserInfo;
import com.lwc.domain.Video;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: ElasticSearchService
 * Description:
 *将数据添加到搜索引擎服务器的相关业务层
 * @Author 林伟朝
 * @Create 2024/10/31 21:07
 */
/*java后端与es搜索引擎进行数据联动的三部曲(前提是已连接上,利用配置类等进行连接),首先定义repository接口,
其内继承了各种操作java类的api,第二部是为需要操作的实体类添加文档注解和字段注解 ,最后一步是将接口注入业务层，真正的开始调用*/
@Service
public class ElasticSearchService {

    @Autowired
    private VideoRepository videoRepository;

    @Autowired
    private UserInfoRepository userInfoRepository;

    //可直接与es服务端进行交互的java客户端依赖
    @Autowired
    private RestHighLevelClient restHighLevelClient;


    public void addVideo(Video video) {
        //repository的父类中直接继承的增删改查方法，甚至不用重写
        videoRepository.save(video);
    }
    public Video getVideo(String keyword) {
        return videoRepository.findByTitleLike(keyword);
    }
    //删除搜索引擎中所有video数据
    public void deleteAllVideos(){
        videoRepository.deleteAll();
    }
    public void addUserInfo(UserInfo userInfo) {
        userInfoRepository.save(userInfo);
    }
    public void deleteAllUserInfos(){
        userInfoRepository.deleteAll();
    }

    /*搜索引擎的全文搜索功能,所谓"全文",意思是模糊匹配多种索引类的信息,比如不止匹配视频信息,还匹配作者信息,
    搜出来的可能是多个实体类的复合信息，比如既要搜出视频信息，又要搜出up主的userInfo,然后联合展示在前端页面上，
    这些符合信息以Map的形式存储,多个Map一起返回构成一页内容,所以外面要用List包裹,有点用多表联查的方式去mysql数据库查询数据的意思*/
    public List<Map<String,Object>>getContents(String keyword,
                                               Integer pageNo,
                                               Integer pageSize) throws IOException {
        //要去搜索引擎服务其中查询的数据类的索引列表
        String[] indices={"videos","user-infos"};
        //新建一个查询请求
        SearchRequest searchRequest=new SearchRequest(indices);
        //查询请求的更多细节方面的配置写进下面这个类中
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();
        //指定展示的索引页和每页的大小
        sourceBuilder.from(pageNo-1);
        sourceBuilder.size(pageSize);
        //下面这个类也是为了存放查询细节....,指定关键字需要匹配的类的哪些text字段
        MultiMatchQueryBuilder matchQueryBuilder= QueryBuilders.multiMatchQuery(keyword,"title","description","nick");
        sourceBuilder.query(matchQueryBuilder);
        searchRequest.source(sourceBuilder);
        //60s查不到东西就报超时异常,以免卡死
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));
        //检查以下三个字段，若字段的值有匹配内容则高亮显示,下面这个数组存储三个需要检查是否有keyword内容需要高亮的字段
        String[] array={"title","description","nick"};
        //新建存放高亮信息的类对象
        HighlightBuilder highlightBuilder=new HighlightBuilder();
        //为高亮存储器添加预留的高亮字段存储空间
        for(String key:array){
            highlightBuilder.fields().add(new HighlightBuilder.Field(key));
        }
        highlightBuilder.requireFieldMatch(false);//要对多个字段进行高亮时需设置为false
        //为将来需要高亮的内容提前设置好样式
        highlightBuilder.preTags("<span style=\"color:red\">");
        highlightBuilder.postTags("</span>");
        //将高亮细节配置类向上封装
        sourceBuilder.highlighter(highlightBuilder);
        //es全文搜索的所有配置均已完成,开始执行搜索
        SearchResponse searchResponse=restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
        //es搜索完成,以下的业务代码将搜索结果按要求提取到List中,然后返回给前端
        List<Map<String,Object>>arrayList=new ArrayList<>();
        for(SearchHit hit:searchResponse.getHits()){
            //遍历搜索引擎中匹配的搜索结果(击中的数据),处理这些击中的数据,主要是高亮字段需要处理
            //提取击中内容的高亮字段集合
            Map<String, HighlightField> highlightBuilderFields = hit.getHighlightFields();
            Map<String, Object> sourceMap = hit.getSourceAsMap();
            //将高亮字段集合中的高亮键一个个取出，看看键值是否为空，非空则将内容提出，放入内容Map中，覆盖原样式
            //每个击中的内容都做这样的高亮处理形成一个个的内容Map
            for(String key:array){
                HighlightField field = highlightBuilderFields.get(key);
                if(field!=null){
                    Text[] fragments = field.fragments();
                    String str = Arrays.toString(fragments);
                    //去除字符串中的"["  和  "]"
                    str=str.substring(1,str.length()-1);
                    sourceMap.put(key,str);
                }
            }
            arrayList.add(sourceMap);
        }
        return arrayList;
    }
}
