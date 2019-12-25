package com.imooc.zuul;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
/*
启动类加上注解@EnableZuulProxy,声明一个Zuul代理
它默认加上@EnableCircuitBreaker
这个代理会使用 Ribbon 获取注册服务的实例，
同时还整合了 Hystrix 实现容错，所有请求都会在 Hystrix 命令中执行。
*/
//启动zuul代理,@EnableZuulServer 非代理方式启动
@EnableZuulProxy
@EnableDiscoveryClient
public class ZuulApplication {
    final static Logger logger = LoggerFactory.getLogger(ZuulApplication.class);

    public static void main(String[] args) {
        // SpringApplication.run(ApiGatewayApplication.class, args);

        ConfigurableApplicationContext applicationContext = new SpringApplicationBuilder(ZuulApplication.class)
                .web(WebApplicationType.SERVLET)
                .run(args);
        // .web(true).run(args);
        logger.debug(applicationContext.getId() + "已经启动,当前host：{}",
                applicationContext.getEnvironment().getProperty("HOSTNAME"));
    }

}
