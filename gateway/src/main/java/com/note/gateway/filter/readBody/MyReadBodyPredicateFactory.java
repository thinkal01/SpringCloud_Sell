package com.note.gateway.filter.readBody;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.handler.AsyncPredicate;
import org.springframework.cloud.gateway.handler.predicate.AbstractRoutePredicateFactory;
import org.springframework.cloud.gateway.support.BodyInserterContext;
import org.springframework.cloud.gateway.support.CachedBodyOutputMessage;
import org.springframework.cloud.gateway.support.DefaultServerRequest;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.function.Predicate;

import static org.springframework.cloud.gateway.filter.AdaptCachedBodyGlobalFilter.CACHED_REQUEST_BODY_KEY;

/**
 *
 */
@Component
@Slf4j
public class MyReadBodyPredicateFactory extends AbstractRoutePredicateFactory<MyReadBodyPredicateFactory.Config> {

    public MyReadBodyPredicateFactory() {
        super(Config.class);
    }

    @Override
    public AsyncPredicate<ServerWebExchange> applyAsync(Config config) {
        return exchange -> {
            Class inClass = config.getInClass();
            if (exchange.getRequest().getHeaders().getContentType().equals(MediaType.APPLICATION_FORM_URLENCODED)) {
                inClass = String.class;
            } else if (exchange.getRequest().getHeaders().getContentType().equals(MediaType.APPLICATION_JSON)) {
                inClass = Object.class;
            }
            if (!exchange.getRequest().getPath().toString().contains("client")) {
                return Mono.just(true);
            }

            /*if (ReadBodyUtil.get() > 1) {
                return Mono.just(true);
            } else {
            }*/

            if (!"CompositeDiscoveryClient_client".equalsIgnoreCase((String) exchange.getAttributes().get(ServerWebExchangeUtils.GATEWAY_PREDICATE_ROUTE_ATTR))) {
                return Mono.just(true);
            }

            ReadBodyUtil.set(ReadBodyUtil.get() + 1);
            ServerRequest serverRequest = new DefaultServerRequest(exchange);
            Mono<?> modifiedBody = serverRequest.bodyToMono(inClass)
                    .flatMap(body -> {
                        log.info("body:{}", body);
                        return Mono.just(body);
                    });

            BodyInserter bodyInserter = BodyInserters.fromPublisher(modifiedBody, inClass);
            CachedBodyOutputMessage outputMessage = new CachedBodyOutputMessage(exchange, exchange.getRequest().getHeaders());
            return bodyInserter.insert(outputMessage, new BodyInserterContext())
                    .then(Mono.defer(() -> {
                        exchange.getAttributes().put(CACHED_REQUEST_BODY_KEY, outputMessage.getBody());
                        return Mono.just(true);
                    }));
        };
    }

    @Override
    public Predicate<ServerWebExchange> apply(Config config) {
        throw new UnsupportedOperationException("MyReadBodyPredicateFactory is only async.");
    }

    @Data
    public static class Config {
        private Class inClass = String.class;
    }
}
