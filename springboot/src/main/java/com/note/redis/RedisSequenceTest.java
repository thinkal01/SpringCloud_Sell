package com.note.redis;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Calendar;
import java.util.Date;

public class RedisSequenceTest {
    /**
     * The message sequence key.
     */
    public static final String sMsgSequenceKeyFormat = "hello";

    @Autowired
    private RedisSequenceFactory mRedisSequenceFactory;

    /**
     * @Description: Get the cache expire time.
     */
    private static Date getTodayEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR_OF_DAY, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime();
    }

    public void test() {
        long v = mRedisSequenceFactory.generate(sMsgSequenceKeyFormat, getTodayEndTime());
    }
}
