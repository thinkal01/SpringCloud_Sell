package com.note.gateway.filter.predicate;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
// @Component
public class TokenRoutePredicateFactory extends AbstractRoutePredicateFactory<TokenRoutePredicateFactory.Config> {
    private static final String DATETIME_KEY = "headerName";

    public TokenRoutePredicateFactory() {
        super(Config.class);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(DATETIME_KEY);
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        log.debug("TokenRoutePredicateFactory Start...");
        return exchange -> {
            //判断header里有放token
            HttpHeaders headers = exchange.getRequest().getHeaders();
            List<String> header = headers.get(config.getHeaderName());
            log.info("Token Predicate headers:{}", header);
            return CollectionUtils.isEmpty(header);
        };
    }

    public static class Config {
        /**
         * 传输token header key
         */
        private String headerName;

        public String getHeaderName() {
            return headerName;
        }

        public void setHeaderName(String headerName) {
            this.headerName = headerName;
        }
    }
}