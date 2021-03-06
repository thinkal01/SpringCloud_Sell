package com.note.gateway;

import com.note.gateway.filter.RequestTimeGatewayFilterFactory;
import com.note.gateway.filter.consistenthash.ConsistentHashClient;
import com.note.gateway.filter.readBody.MyReadBodyPredicateFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.gateway.discovery.DiscoveryLocatorProperties;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;

import static org.springframework.cloud.gateway.support.NameUtils.normalizeRoutePredicateName;

@SpringCloudApplication
@Slf4j
public class GateWayApplication {
    public static void main(String[] args) {
        SpringApplication.run(GateWayApplication.class, args);
    }

    // @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // .route(p -> p.path("/getHashValueExchange")
                //         // e代表着ServerWebExchange对象，可以通过ServerWebExchange获取所有请求相关的信息，例如Cookies和Headers。
                //         // 通过Lambda语法去编写判断逻辑，如果一个Route中所有的Predicate返回的结果都是TRUE则匹配成功，否则匹配失败。
                //         // path和predicate需要使用and,or链接，分别代表不同的逻辑运算！
                //         .and()
                //         .predicate(e -> e.getClass() != null)
                //         .filters(f -> f.addRequestHeader("Hello", "World"))
                //         .uri("http://localhost:8866")
                // ).route(r -> r.path("/user/**")
                //         .filters(fn -> fn.addResponseHeader("developer", "Nico"))
                //         .uri("lb://USER_SERVER_NAME")
                //         .filters(new AuthorizeGatewayFilter())
                //         .order(0)
                //         .id("user-service"))
                // .route(p -> p.host("*.lemon.com")
                //         .filters(f -> f.hystrix(config -> config.setName("test").setFallbackUri("forward:/fallback")))
                //         .uri("http://localhost:8855"))
                .build();
    }

    // 注册RequestTimeGatewayFilterFactory类的Bean
    // @Bean
    public RequestTimeGatewayFilterFactory elapsedGatewayFilterFactory() {
        return new RequestTimeGatewayFilterFactory();
    }

    @Bean
    public ConsistentHashClient consistentHashClient() {
        return new ConsistentHashClient();
    }

    @Autowired
    private DiscoveryLocatorProperties properties;

    @PostConstruct
    public void myFactory() {
        PredicateDefinition predicate = new PredicateDefinition();
        predicate.setName(normalizeRoutePredicateName(MyReadBodyPredicateFactory.class));
        // predicate.addArg("", "'/'+serviceId+'/**'");
        properties.getPredicates().add(predicate);
    }
}