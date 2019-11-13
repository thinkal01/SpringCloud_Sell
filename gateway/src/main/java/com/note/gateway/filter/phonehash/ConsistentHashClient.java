package com.note.gateway.filter.phonehash;

import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.shared.Application;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

@Slf4j
public class ConsistentHashClient {
    @Autowired
    private SpringClientFactory springClientFactory;
    // service和对应的一致性hash处理类缓存
    private Map<String, ConsistentHashWithVirtualNode> serverMap = new ConcurrentHashMap<>();
    // 所有service同步锁
    Lock lock = new ReentrantLock();
    // service内同步锁
    Map<String, Lock> serviceLockMap = new HashMap<>();

    // 获取服务
    public String getServer(String serviceId, String phone) {
        ConsistentHashWithVirtualNode server = getConsistentHashWithVirtualNode(serviceId);
        String host = server.getServer(phone);
        return host;
    }

    private ConsistentHashWithVirtualNode getConsistentHashWithVirtualNode(String serviceId) {
        // 统一采用小写
        serviceId = serviceId.toLowerCase();
        ConsistentHashWithVirtualNode server = serverMap.get(serviceId);
        if (server != null) {
            return server;
        }

        Lock serviceLock = getServiceLock(serviceId);
        // 加锁,防止并发重复创建service一致性hash处理类
        serviceLock.lock();
        // 获取锁之后,从map中获取一次
        server = serverMap.get(serviceId);
        if (server != null) {
            return server;
        }

        List<Server> allServers = springClientFactory.getLoadBalancer(serviceId).getAllServers();
        List<String> hostList = allServers.stream().map(Server::getHostPort).collect(Collectors.toList());
        ConsistentHashWithVirtualNode newServer = new ConsistentHashWithVirtualNode(hostList);
        serverMap.put(serviceId, newServer);
        // 解锁
        serviceLock.unlock();

        return newServer;
    }

    /**
     * 获取服务service对应的锁
     * @param serviceId
     * @return
     */
    private Lock getServiceLock(String serviceId) {
        Lock serviceLock = serviceLockMap.get(serviceId);
        if (serviceLock == null) {
            lock.lock();
            serviceLock = serviceLockMap.get(serviceId);
            if (serviceLock == null) {
                serviceLock = new ReentrantLock();
                serviceLockMap.put(serviceId, serviceLock);
            }
            lock.unlock();
        }

        return serviceLock;
    }

    /**
     * 获取服务列表缓存
     * @param serviceId
     * @return
     */
    public List<String> getServiceServerList(String serviceId) {
        serviceId = serviceId.toLowerCase();
        ConsistentHashWithVirtualNode consistentHashWithVirtualNode = serverMap.get(serviceId);
        if (consistentHashWithVirtualNode != null) {
            return consistentHashWithVirtualNode.getServerList();
        }

        return Collections.EMPTY_LIST;
    }

    /**
     * 更新应用
     * @param application
     */
    public void refreshApplication(Application application) {
        String applicationName = application.getName().toLowerCase();
        ConsistentHashWithVirtualNode consistentHashWithVirtualNode = serverMap.get(applicationName);
        List<String> preServerList = Collections.EMPTY_LIST;
        if (consistentHashWithVirtualNode != null) {
            preServerList = consistentHashWithVirtualNode.getServerList();
        }

        List<String> afterServerList = application.getInstances().stream().map(InstanceInfo::getInstanceId).collect(Collectors.toList());
        serverMap.put(applicationName, new ConsistentHashWithVirtualNode(afterServerList));
        log.info("{}应用列表缓存被更新，更新前列表{}，更新后列表{}", applicationName, preServerList, afterServerList);
    }
}
