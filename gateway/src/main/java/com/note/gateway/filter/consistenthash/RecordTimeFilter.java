package com.note.gateway.filter.consistenthash;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.note.gateway.filter.consistenthash.ConsistentHashUtils.FILTER_START_TIME;
import static com.note.gateway.filter.consistenthash.ConsistentHashUtils.REQUEST_START_TIME;

@Slf4j
@Component
public class RecordTimeFilter implements GlobalFilter, Ordered {
    /**
     * 输出请求过滤和响应时长
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        Long filterStartTime = exchange.getAttribute(FILTER_START_TIME);
        long currentTimeMillis = System.currentTimeMillis();
        log.info("所有过滤器处理耗时：{}ms", currentTimeMillis - filterStartTime);
        // 先记录请求开始时间，并保存在ServerWebExchange中，此处是一个“pre”类型过滤器
        exchange.getAttributes().put(REQUEST_START_TIME, currentTimeMillis);
        // 然后再chain.filter的内部类中的run()方法中相当于”post”过滤器，在此处打印了请求所消耗的时间。
        return chain.filter(exchange).then(
                Mono.fromRunnable(() -> {
                    Long startTime = exchange.getAttribute(REQUEST_START_TIME);
                    Long endTime = (System.currentTimeMillis() - startTime);
                    if (startTime != null) {
                        // o.s.cloud.gateway.filter.GatewayFilter : /test: 5633ms
                        log.info("请求服务耗时{}", exchange.getRequest().getURI().getRawPath() + ": " + endTime + "ms");
                    }
                })
        );
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}