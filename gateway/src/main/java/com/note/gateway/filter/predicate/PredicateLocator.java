package com.note.gateway.filter.predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// @Configuration
public class PredicateLocator {
    @Autowired
    private TokenRoutePredicateFactory tokenRoutePredicateFactory;

    @Bean
    public RouteLocator myRoutes(RouteLocatorBuilder builder) {
        return builder.routes().route(predicateSpec -> predicateSpec.path("/path/**")
                .and().asyncPredicate(tokenRoutePredicateFactory.applyAsync(config -> config.setHeaderName("Authorization")))
                .uri("http://localhost:8080")
                .id("user-service"))
                .build();
    }
}
