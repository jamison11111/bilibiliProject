package com.lwc.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lwc.Dao.UserMomentDao;
import com.lwc.Dao.UserMomentDao;
import com.lwc.domain.UserMoment;
import com.lwc.domain.constant.UserMomentsConstant;
import com.lwc.service.util.RocketMQUtil;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

/**
 * ClassName: UserMomentsServices
 * Description:
 *
 * @Author 林伟朝
 * @Create 2024/10/14 10:14
 */
@Service
public class UserMomentService {
    @Autowired
    private UserMomentDao userMomentDao;

    //生产者和消费者是配置类里的bean,这类bean的引入方式之一是通过上下文对象的方式引入
    @Autowired
    ApplicationContext applicationContext;


    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    public void addUserMoments(UserMoment userMoment) throws Exception {
        userMoment.setCreateTime(new Date());
        userMomentDao.addUserMoments(userMoment);
        //除了将动态发布到数据库外,还要将该消息发到rocketMQ,让消费者将其推送给粉丝们
        //生产者发送消息给MQ,告知订阅的消费者,某条动态被发布到数据库,可以去查阅了
        //生产者和消费者是配置类里的bean,这类bean的引入方式之一是通过上下文对象的方式引入
        DefaultMQProducer producer = (DefaultMQProducer)applicationContext.getBean("momentsProducer");
        Message msg = new Message(UserMomentsConstant.TOPIC_MOMENTS, JSONObject.toJSONString(userMoment).getBytes(StandardCharsets.UTF_8));
        RocketMQUtil.syncSendMsg(producer,msg);
    }

    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        //在redis里快速查找,而不用去数据库进行io操作
        String key="subscribed-"+userId;
        String subscribedList = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(subscribedList, UserMoment.class);
    }
}
