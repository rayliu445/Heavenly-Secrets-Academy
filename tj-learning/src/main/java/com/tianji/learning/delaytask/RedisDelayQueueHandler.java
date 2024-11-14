package com.tianji.learning.delaytask;

public interface RedisDelayQueueHandler<T>{

    void execute(T t);
}
