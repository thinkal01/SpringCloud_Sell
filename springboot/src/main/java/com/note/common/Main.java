package com.note.common;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Main {

    public static void main(String[] args) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ThreadConfig.class);
        AsynTaskService service = context.getBean(AsynTaskService.class);
        for (int i = 0; i < 10; i++) {
            service.f1(); // 执行异步任务
        }
        context.close();
    }
}  