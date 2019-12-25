package com.imooc.zuul.consistent;

import com.netflix.discovery.shared.Application;
import com.netflix.loadbalancer.Server;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;

/**
 * 一致性hash处理类
 */
@Slf4j
@RefreshScope
@Component
public class ConsistentHashClient {
    @Autowired
    private SpringClientFactory springClientFactory;

    // @Value("#{${consistentHashService:{client:'phone'}}}")
    @Value("#{${consistent.hash.service}}")
    private Map<String, String> consistentHashService;

    // service和对应的一致性hash处理类缓存
    private Map<String, ConsistentHashWithVirtualNode> serverMap = new ConcurrentHashMap<>();

    // 所有service同步锁
    Lock lock = new ReentrantLock();

    // service内同步锁
    Map<String, Lock> serviceLockMap = new HashMap<>();

    /**
     * 获得一致性hash服务
     * @param serviceId
     * @param hashValue
     * @return
     */
    public String getServer(String serviceId, String hashValue) {
        ConsistentHashWithVirtualNode server = getConsistentHashWithVirtualNode(serviceId);
        String host = server.getServer(hashValue);
        return host;
    }

    /**
     * 根据serviceId获得对应的一致性hash对象
     * @param serviceId
     * @return
     */
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
     * 获取hash分发key
     * @param serviceId
     * @return
     */
    public String getHashKey(String serviceId) {
        if (StringUtils.isBlank(serviceId)) {
            return "";
        }

        if (CollectionUtils.isEmpty(consistentHashService)) {
            return "";
        }

        serviceId = serviceId.toLowerCase();
        for (String serviceItem : consistentHashService.keySet()) {
            if (StringUtil.matchIgnoreCase(serviceId, serviceItem)) {
                return consistentHashService.get(serviceItem);
            }
        }

        return "";
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

        List<String> afterServerList = application.getInstances().stream().map(instanceInfo -> instanceInfo.getIPAddr() + ":" + instanceInfo.getPort()).collect(Collectors.toList());
        serverMap.put(applicationName, new ConsistentHashWithVirtualNode(afterServerList));
        log.info("{}应用列表缓存被更新，更新前列表{}，更新后列表{}", applicationName, preServerList, afterServerList);
    }

    /**
     * 清理一致性hash缓存类,清除非hash处理service
     */
    public void cleanServerMap() {
        Iterator<String> serviceIterator = serverMap.keySet().iterator();
        while (serviceIterator.hasNext()) {
            String serviceId = serviceIterator.next();
            if (!isConsistentHashService(serviceId)) {
                serviceIterator.remove();
            }
        }
    }

    /**
     * 是否为一致性hash处理service
     * @param serviceId
     * @return
     */
    public boolean isConsistentHashService(String serviceId) {
        return StringUtils.isNotBlank(getHashKey(serviceId));
    }
}
