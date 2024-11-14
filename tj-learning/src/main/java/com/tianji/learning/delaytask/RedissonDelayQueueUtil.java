package com.tianji.learning.delaytask;


import com.tianji.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RBlockingDeque;
import org.redisson.api.RDelayedQueue;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedissonDelayQueueUtil {

    @Autowired
    private RedissonClient redissonClient;


    /**
     * 添加延迟队列
     *
     * @param value     队列值
     * @param delay     延迟时间
     * @param timeUnit  时间单位
     * @param queueCode 队列键
     * @param <T>
     */
    public <T> boolean addDelayTask(@NotNull T value, @NonNull long delay, @NonNull TimeUnit timeUnit, @NonNull String queueCode){
        if(StringUtils.isBlank(queueCode) || Objects.isNull(value)){
            return false;
        }
        try {
            RBlockingDeque<Object> blockingDequeue= redissonClient.getBlockingDeque(queueCode);
            RDelayedQueue<Object> delayedQueue= redissonClient.getDelayedQueue(blockingDequeue);
            delayedQueue.offer(value,delay,timeUnit);
            log.info("添加延时队列成功-队列键:{},队列值:{},延迟时间:{}",queueCode,value,TimeUnit.MICROSECONDS.toSeconds(delay)+"秒");
        } catch (Exception e) {
            log.error("添加延时队列失败{}",e.getMessage());
            throw new RuntimeException(e);
        }
        return true;
    }


    /**
     * 获取延迟队列
     *
     * @param queueCode
     * @param <T>
     */
    public <T> T getDelayQueue(@NonNull String queueCode) throws InterruptedException{
        if(StringUtils.isBlank(queueCode)){
            return null;
        }
        RBlockingDeque<Map> blockingDeque = redissonClient.getBlockingDeque(queueCode);
        RDelayedQueue<Map> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        T value = (T) delayedQueue.poll();
        return value;
    }


    /**
     * 删除指定队列中的消息
     *
     * @param o 指定删除的消息对象队列值(同队列需保证唯一性)
     * @param queueCode 指定队列键
     */
    public boolean removDelayedQueue(@NonNull Object o,@NotNull String queueCode){
        if(StringUtils.isBlank(queueCode) || Objects.isNull(o)){
            return false;
        }
        RBlockingDeque<Object> blockingDeque=redissonClient.getBlockingDeque(queueCode);
        RDelayedQueue<Object> delayedQueue = redissonClient.getDelayedQueue(blockingDeque);
        boolean flag = delayedQueue.remove(o);
        return flag;
    }

}
