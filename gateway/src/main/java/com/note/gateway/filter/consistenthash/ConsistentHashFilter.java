package com.note.gateway.filter.consistenthash;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

import static com.note.gateway.filter.consistenthash.ConsistentHashUtils.HASH_VALUE;
import static org.springframework.cloud.gateway.filter.LoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Slf4j
@Component
public class ConsistentHashFilter implements GlobalFilter, Ordered {
    @Autowired
    private ConsistentHashClient consistentHashClient;

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER - 10;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        try {
            // 获取hash分发值
            String hashValue = exchange.getAttribute(HASH_VALUE);
            if (StringUtils.isBlank(hashValue)) {
                return chain.filter(exchange);
            }

            URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
            // 服务id
            String serviceId = url.getHost();
            // 一致性hash所得服务主机
            String host = consistentHashClient.getServer(serviceId, hashValue);
            if (StringUtils.isBlank(host)) {
                log.warn("一致性hash所得服务主机为空,可能由于修改配置文件所致");
                return chain.filter(exchange);
            }

            URI uri = exchange.getRequest().getURI();
            URI requestUrl = URI.create("http://" + host + uri.getRawPath());
            log.info("一致性hash分发，hash值为{}，请求地址为{}", hashValue, requestUrl);

            // 设置请求地址
            exchange.getAttributes().put(GATEWAY_REQUEST_URL_ATTR, requestUrl);
        } catch (Exception e) {
            log.error("一致性hash分发出现异常，兜底采用网关默认分发规则", e);
        } finally {
            return chain.filter(exchange);
        }
    }
}
