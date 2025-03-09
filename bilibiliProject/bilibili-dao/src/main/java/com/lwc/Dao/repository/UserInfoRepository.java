package com.lwc.Dao.repository;

import com.lwc.domain.UserInfo;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * ClassName: UserInfoRepository
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/11/1 16:07
 */
public interface UserInfoRepository extends ElasticsearchRepository<UserInfo, Long> {

}
