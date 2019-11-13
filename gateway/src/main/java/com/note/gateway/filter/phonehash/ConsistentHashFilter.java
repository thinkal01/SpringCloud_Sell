package com.note.gateway.filter.phonehash;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;
import java.util.Map;

import static org.springframework.cloud.gateway.filter.LoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Slf4j
@Component
@RefreshScope
public class ConsistentHashFilter implements GlobalFilter, Ordered {
    @Autowired
    private ConsistentHashClient consistentHashClient;

    @Value("#{${consistentHashService}}")
    private Map<String, String> consistentHashService;

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER - 10;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("进入一致性hash过滤器，请求地址为{}，请求参数为{}", JSON.toJSONString(exchange.getRequest().getURI()), JSON.toJSONString(exchange.getRequest().getQueryParams()));
        try {
            URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
            String serviceId = url.getHost();
            String hashKey = consistentHashService.get(serviceId.toLowerCase());
            if (StringUtils.isBlank(hashKey)) {
                // 非一致性hash处理service
                return chain.filter(exchange);
            }

            // 获取hash分发值
            List<String> hashValueList = exchange.getRequest().getQueryParams().get(hashKey);
            if (CollectionUtils.isEmpty(hashValueList)) {
                log.warn("一致性hash分发{}值为空", hashKey);
                return chain.filter(exchange);
            }

            String hashValue = hashValueList.get(0);
            // 一致性hash所得服务主机
            String host = consistentHashClient.getServer(serviceId, hashValue);
            URI uri = exchange.getRequest().getURI();
            URI requestUrl = URI.create("http://" + host + uri.getRawPath());
            log.info("一致性hash分发，hash值为{}，请求地址为{}", hashValue, requestUrl);
            // 测试异常兜底逻辑
            // if (true) {
            //     throw new RuntimeException();
            // }

            // 设置请求地址
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        } catch (Exception e) {
            log.error("一致性hash分发出现异常，兜底采用网关默认分发规则", e);
        } finally {
            return chain.filter(exchange);
        }
    }
}
