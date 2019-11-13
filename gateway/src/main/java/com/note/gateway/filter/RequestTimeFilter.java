package com.note.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
public class RequestTimeFilter implements GatewayFilter, Ordered {
    private static final String COUNT_START_TIME = "requestTimeBegin";

    /**
     * 输出请求响应时长
     * 对路由转发耗时进行统计
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 先记录请求开始时间，并保存在ServerWebExchange中，此处是一个“pre”类型过滤器
        exchange.getAttributes().put(COUNT_START_TIME, System.currentTimeMillis());
        // 然后再chain.filter的内部类中的run()方法中相当于”post”过滤器，在此处打印了请求所消耗的时间。
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    Long startTime = exchange.getAttribute(COUNT_START_TIME);
                    Long endTime = (System.currentTimeMillis() - startTime);
                    if (startTime != null) {
                        // o.s.cloud.gateway.filter.GatewayFilter : /test: 5633ms
                        log.info(exchange.getRequest().getURI().getRawPath() + ": " + endTime + "ms");
                    }
                })
        );
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}