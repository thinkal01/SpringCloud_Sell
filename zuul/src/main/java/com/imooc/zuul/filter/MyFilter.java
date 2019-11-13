package com.imooc.zuul.filter;

import org.springframework.cloud.netflix.zuul.filters.ProxyRequestHelper;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.pre.PreDecorationFilter;

import java.lang.reflect.Field;

public class MyFilter extends PreDecorationFilter {

    public MyFilter(RouteLocator routeLocator, String dispatcherServletPath, ZuulProperties properties, ProxyRequestHelper proxyRequestHelper) throws Exception {
        super(routeLocator, dispatcherServletPath, properties, proxyRequestHelper);
        Field urlPathHelper = PreDecorationFilter.class.getDeclaredField("urlPathHelper");
        urlPathHelper.setAccessible(true);
        urlPathHelper.set(this, new MyUrlPathHelper());
    }

}
