package com.note.common;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class AsynTaskService {

    // 标注为异步任务，执行此方法时，会单独开启线程来执行
    @Async
    public void f1() {
        System.out.println("Thread.currentThread().getName(): " + Thread.currentThread().getName());
        try {
            Thread.sleep(new Random().nextInt(100));
        } catch (InterruptedException e) {
        }
    }
}