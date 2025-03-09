package com.lwc.Dao;

import com.lwc.domain.UserCoin;
import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: UserCoinDao
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/23 22:08
 */
@Mapper
public interface UserCoinDao {
    Integer getUserCoinsAmount(Long userId);

    Integer updateUserCoin(UserCoin userCoin);
}
