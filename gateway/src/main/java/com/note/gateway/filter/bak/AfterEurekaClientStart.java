package com.note.gateway.filter.bak;

import com.netflix.appinfo.ApplicationInfoManager;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import com.netflix.discovery.StatusChangeEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 在Eureka Client 端，通过@PostConstruct注解去做一些初始化工作，
 * 有时候会涉及到调用其他微服务，由于Eureka Client尚未启动完成，注册信息还没有从Eureka Server上拉取下来，
 * 因此ribbon获取不到client信息，在使用Feign调用的过程中，如果开启了熔断器，服务调用会快速失败
 * feign:
 *   hystrix:
 *     enabled: true
 * 如果去除如上配置，那么此次调用等获取到了Eureka Client信息之后才开始调用。
 * 为保证在使用了Hystrix的前提下，能够在系统启动成功之后，调用其他微服务做一些初始化工作
 */
// @Component
public class AfterEurekaClientStart {
    ApplicationInfoManager.StatusChangeListener eurekaListener = null;
    @Resource
    private EurekaClient eurekaClient;
    @Autowired
    private ApplicationInfoManager applicationInfoManager;

    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    @PostConstruct
    public void init() {
        eurekaListener = new ApplicationInfoManager.StatusChangeListener() {
            // Id可以自定义
            @Override
            public String getId() {
                return "forExample";
            }

            @Override
            public void notify(StatusChangeEvent statusChangeEvent) {
                // 当前状态为UP， 之前的状态为STARTING
                if (InstanceInfo.InstanceStatus.UP == statusChangeEvent.getStatus() && InstanceInfo.InstanceStatus.STARTING == statusChangeEvent.getPreviousStatus()) {
                    // 执行一遍后，卸载该监听，达到在EurekaClient启动完成后，仅执行一次
                    executorService.execute(() -> {
                        while (!Thread.currentThread().isInterrupted()) {
                            try {
                                System.out.println("=================调用服务进行初始化=================");
                                Thread.sleep(100);
                            } catch (Exception e) {
                            }
                        }
                    });
                    applicationInfoManager.unregisterStatusChangeListener(eurekaListener.getId());
                    eurekaListener = null;
                }
            }
        };

        eurekaClient.getApplicationInfoManager().registerStatusChangeListener(eurekaListener);
    }
}