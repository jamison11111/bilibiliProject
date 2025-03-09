package com.lwc.Dao.repository;

import com.lwc.domain.Video;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ClassName: VideoRepository
 * Description:
 *类似于Dao中的Mapper这个接口中继承的类不仅会被spring项目自动生成实现类，而且其内还有很多
 * 现成的增删改查方法可供直接调用
 * @Author 林伟朝
 * @Create 2024/10/31 21:16
 */
public interface VideoRepository extends ElasticsearchRepository<Video,Long> {

    //不是本接口继承下来的基础方法，但这个接口内置只能拆解功能,可以自动拆解生成方法
    //find by title like
    Video findByTitleLike(String keyword);
}
