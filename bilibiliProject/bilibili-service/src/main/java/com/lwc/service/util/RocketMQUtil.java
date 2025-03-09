package com.lwc.service.util;

import org.apache.rocketmq.client.exception.MQBrokerException;
import org.apache.rocketmq.client.exception.MQClientException;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.CountDownLatch2;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.exception.RemotingException;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * ClassName: RocketMQUtil
 * Description:
 *RocketMQ相关操作的工具类,主要是生产者发送消息的一些方法api
 * @Author 林伟朝
 * @Create 2024/10/12 17:13
 */

/*服务层编写好MQ的配置类(定义好项目的生产者和消费者)和工具类(定义好MQ生产者发送消息的API)*/
public class RocketMQUtil {

    //同步发送
    public static void syncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {
        SendResult sendResult = producer.send(msg);
        System.out.println(sendResult);
    }

    //异步发送且有回调函数
    public static void asyncSendMsg(DefaultMQProducer producer, Message msg) throws Exception {
        int messageCount=2;//异步发送的消息的条数
        CountDownLatch2 countDownLatch=new CountDownLatch2(messageCount);
        for(int i=0;i<messageCount;i++) {
            producer.send(msg, new SendCallback() {
                @Override
                public void onSuccess(SendResult sendResult) {
                    //倒计时器开始转动
                    countDownLatch.countDown();
                    System.out.println(sendResult.getMsgId());
                }

                @Override
                public void onException(Throwable e) {
                    //倒计时器开始转动
                    countDownLatch.countDown();
                    System.out.println("发送异步消息时发生了异常!" + e);
                    e.printStackTrace();
                }
            });
        }
        //计时器等待5s
        countDownLatch.await(5, TimeUnit.SECONDS);
        }
    }

