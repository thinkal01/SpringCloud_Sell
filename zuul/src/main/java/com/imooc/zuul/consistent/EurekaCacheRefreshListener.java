package com.imooc.zuul.consistent;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.cloud.netflix.eureka.CloudEurekaClient;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Description: 自定义监听器，接收缓存刷新的广播
 */
@Component
@Slf4j
public class EurekaCacheRefreshListener implements ApplicationListener<HeartbeatEvent> {
    @Autowired
    private ConsistentHashClient consistentHashClient;

    @Override
    public void onApplicationEvent(HeartbeatEvent event) {
        // log.info("接收到缓存刷新广播");
        Object source = event.getSource();
        CloudEurekaClient eurekaClient = (CloudEurekaClient) source;
        consistentHashClient.cleanServerMap();
        List<Application> applicationList = eurekaClient.getApplications().getRegisteredApplications();

        applicationList.forEach(application -> {
            // 是一致性hash应用且发生变化时,更新应用缓存
            if (consistentHashClient.isConsistentHashService(application.getName())
                    && isApplicationChanged(application)) {
                consistentHashClient.refreshApplication(application);
            }
        });
        Object count = event.getValue();
        // log.info("start onApplicationEvent, count [{}], source :\n{}", count, JSON.toJSON(eurekaClient.getApplications().getRegisteredApplications()));
    }

    private boolean isApplicationChanged(Application application) {
        List<String> serviceServerList = consistentHashClient.getServiceServerList(application.getName());
        if (serviceServerList.size() != application.getInstances().size()) {
            // 集合长度不等,说明应用已发生变化
            return true;
        }

        for (InstanceInfo instanceInfo : application.getInstances()) {
            // boolean match = serviceServerList.stream().anyMatch(instanceInfo.getInstanceId()::equals);
            if (!serviceServerList.contains(instanceInfo.getIPAddr() + ":" + instanceInfo.getPort())) {
                // 任一个不匹配,说明应用已发生变化
                return true;
            }
        }

        return false;
    }
}