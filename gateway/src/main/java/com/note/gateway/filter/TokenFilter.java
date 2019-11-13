package com.note.gateway.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 定义一个全局过滤器，对请求到网关的URL进行权限校验，判断请求的URL是否是合法请求。
 * 全局过滤器处理逻辑是通过从Gateway上下文ServerWebExchange对象中获取authToken对应的值进行判Null处理。
 */
// @Component
@Slf4j
public class TokenFilter implements GlobalFilter, Ordered {
    /**
     * 拦截请求，获取authToken，并校验
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String token = exchange.getRequest().getQueryParams().getFirst("authToken");
        if (null == token || token.isEmpty()) {
            log.info("token is empty...");
            //当请求不携带Token或者token为空时，直接设置请求状态码为401，返回
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return -400;
    }
}