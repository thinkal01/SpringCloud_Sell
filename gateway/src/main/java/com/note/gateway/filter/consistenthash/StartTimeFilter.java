package com.note.gateway.filter.consistenthash;

import com.note.gateway.filter.readBody.ReadBodyUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import static com.note.gateway.filter.consistenthash.ConsistentHashUtils.FILTER_START_TIME;

@Slf4j
@Component
public class StartTimeFilter implements GlobalFilter, Ordered {

    /**
     * 记录开始执行过滤器时间
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ReadBodyUtil.remove();
        // 先记录过滤开始时间，并保存在ServerWebExchange中，此处是一个“pre”类型过滤器
        exchange.getAttributes().put(FILTER_START_TIME, System.currentTimeMillis());
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}