package com.imooc.zuul.config;

import com.imooc.zuul.filter.MyFilter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

// @Component
public class ZuulConfig {
    /*@ConfigurationProperties("zuul")
    @RefreshScope
    public ZuulProperties zuulProperties() {
        return new ZuulProperties();
    }*/

    // pre filters
    @Bean
    @ConditionalOnMissingBean(PreDecorationFilter.class)
    public PreDecorationFilter preDecorationFilter(RouteLocator routeLocator, ServerProperties server, ProxyRequestHelper proxyRequestHelper, ZuulProperties zuulProperties) throws Exception {
        return new MyFilter(routeLocator, server.getServlet().getServletPrefix(), zuulProperties, proxyRequestHelper);
    }
}
