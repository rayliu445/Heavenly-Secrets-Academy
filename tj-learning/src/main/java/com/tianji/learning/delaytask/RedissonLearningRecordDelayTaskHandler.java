package com.tianji.learning.delaytask;

import java.util.Map;

public class RedissonLearningRecordDelayTaskHandler implements RedisDelayQueueHandler<Map>{

    @Override
    public void execute(Map map) {
        //dosomething
    }
}
