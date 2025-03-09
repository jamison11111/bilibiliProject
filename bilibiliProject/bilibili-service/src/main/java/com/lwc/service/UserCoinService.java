package com.lwc.service;

import com.lwc.Dao.UserCoinDao;
import com.lwc.domain.UserCoin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * ClassName: UserCoinService
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/23 22:06
 */
@Service
public class UserCoinService {
    @Autowired
    private UserCoinDao userCoinDao;



    public Integer getUserCoinsAmount(Long userId) {
        return userCoinDao.getUserCoinsAmount(userId);
    }

    public void updateUserCoin(Long userId, Long coinsAmount) {
        UserCoin userCoin = new UserCoin();
        userCoin.setUserId(userId);
        userCoin.setAmount(coinsAmount);
        userCoin.setUpdateTime(new Date());
        userCoinDao.updateUserCoin(userCoin);
    }
}
