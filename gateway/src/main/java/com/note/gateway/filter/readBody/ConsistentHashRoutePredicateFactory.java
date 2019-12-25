package com.note.gateway.filter.readBody;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.handler.predicate.ReadBodyPredicateFactory;
import org.springframework.web.server.ServerWebExchange;

import java.util.Collections;
import java.util.List;
import java.util.function.Predicate;

@Slf4j
// @Component
public class ConsistentHashRoutePredicateFactory extends AbstractRoutePredicateFactory<ConsistentHashRoutePredicateFactory.Config> {
    public static final String DATETIME_KEY = "datetime";
    @Autowired
    private ReadBodyPredicateFactory readBodyPredicateFactory;

    public ConsistentHashRoutePredicateFactory() {
        super(ConsistentHashRoutePredicateFactory.Config.class);
    }

    @Override
    public AsyncPredicate<ServerWebExchange> applyAsync(ConsistentHashRoutePredicateFactory.Config config) {
        // log.info("request method POST, Content-Type is application/x-www-form-urlencoded, body  is:{}", readBody);
        // return config2 -> return true;
        ReadBodyPredicateFactory.Config config2 = new ReadBodyPredicateFactory.Config();
        config2.setInClass(String.class);
        config2.setPredicate(readbody -> true);
        return readBodyPredicateFactory.applyAsync(config2);
    }

    @Override
    public Predicate<ServerWebExchange> apply(ConsistentHashRoutePredicateFactory.Config config) {
        throw new UnsupportedOperationException(
                "MyReadBodyPredicateFactory is only async.");
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return Collections.singletonList(DATETIME_KEY);
    }

    public static class Config {
        private String datetime;

        public String getDatetime() {
            return datetime;
        }

        public void setDatetime(String datetime) {
            this.datetime = datetime;
        }
    }
}