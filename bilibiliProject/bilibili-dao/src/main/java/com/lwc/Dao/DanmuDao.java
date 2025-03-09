package com.lwc.Dao;

import com.lwc.domain.Danmu;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.scheduling.annotation.Async;

import java.util.List;
import java.util.Map;

/**
 * ClassName: DanmuDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/28 14:14
 */
@Mapper
public interface DanmuDao {

    Integer addDanmu(Danmu danmu);

    List<Danmu> getDanmus(Map<String, Object> params);


}
