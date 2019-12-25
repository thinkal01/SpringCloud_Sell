package com.imooc.zuul.route;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.netflix.zuul.filters.RefreshableRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.Route;
import org.springframework.cloud.netflix.zuul.filters.SimpleRouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * eureka资源路由
 */
@Slf4j
public class EurekaRouteLocator extends SimpleRouteLocator implements RefreshableRouteLocator {
    private ZuulProperties properties;

    public EurekaRouteLocator(String servletPath, ZuulProperties properties) {
        super(servletPath, properties);
        this.properties = properties;
    }

    @Override
    protected Map<String, ZuulProperties.ZuulRoute> locateRoutes() {
        LinkedHashMap<String, ZuulProperties.ZuulRoute> routesMap = new LinkedHashMap<>(super.locateRoutes());

        //路由信息
        String serviceId = "pa-eureka";
        String path = "/eureka/**";
        ZuulProperties.ZuulRoute zuulRoute = new ZuulProperties.ZuulRoute();
        //服务提供者的id
        zuulRoute.setServiceId(serviceId);
        zuulRoute.setId("eurekaId");
        //匹配的路径
        zuulRoute.setPath(path);
        routesMap.put(path, zuulRoute);

        return routesMap;
    }

    @Override
    protected Route getRoute(ZuulProperties.ZuulRoute route, String path) {
        Route zuulRoute = super.getRoute(route, path);
        if (zuulRoute == null) {
            return null;
        }
        String prefix = zuulRoute.getPrefix().replace(this.properties.getPrefix(), "");
        zuulRoute.setPrefix(prefix);
        String fullPath = zuulRoute.getFullPath().replace(this.properties.getPrefix(), "");
        zuulRoute.setFullPath(fullPath);
        return zuulRoute;
    }

    @Override
    public void refresh() {

    }
}