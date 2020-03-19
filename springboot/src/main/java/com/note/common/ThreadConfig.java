package com.note.common;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * 线程池的配置还有一种方式，就是直接实现AsyncConfigurer接口，重写getAsyncExecutor方法
 */
@Configuration
@EnableAsync
@ComponentScan("com.note.common")
public class ThreadConfig implements AsyncConfigurer {

    /*
    线程池参数
     */
    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

        //核心线程池数量，返回可用处理器核心数。
        executor.setCorePoolSize(Runtime.getRuntime().availableProcessors());
        //最大线程数量
        executor.setMaxPoolSize(Runtime.getRuntime().availableProcessors() * 5);
        //线程池队列容量
        executor.setQueueCapacity(Runtime.getRuntime().availableProcessors() * 2);
        // 线程数大于corePoolSize，如果某线程空闲时间超过keepAliveTime，线程将被终止
        executor.setKeepAliveSeconds(60);
        //线程名称前缀
        executor.setThreadNamePrefix("this-excutor-");
        //关闭时是否等待任务完成
        executor.setWaitForTasksToCompleteOnShutdown(true);
        // setRejectedExecutionHandler：当pool已经达到max size的时候，如何处理新任务
        // CallerRunsPolicy：不在新线程中执行任务，而是由调用者所在线程来执行
        // executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        //执行初始化
        executor.initialize();
        return executor;
    }

    /*异步任务中异常处理*/
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }
}