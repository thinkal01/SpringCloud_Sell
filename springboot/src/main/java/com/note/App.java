package com.note;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;

/*
 * SpringBoot 启动类
 */
@SpringBootApplication
/**
 * SpringBoot整合Servlet,Filter,Listener方式一
 * 在springBoot启动时会扫描@WebServlet，@WebFilter,@WebListener并将该类实例化
 */
// @ServletComponentScan
@MapperScan("springboot.mybatis.mapper") // 扫描MyBatis的Mapper接口
// 开启缓存
@EnableCaching
// 开启任务调度
@EnableScheduling
// App 文件不能直接放在main/java文件夹下，必须要建一个包把他放进去
public class App {

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    // SpringBoot整合Servlet方式二
    @Bean
    public ServletRegistrationBean getServletRegistrationBean() {
        ServletRegistrationBean bean = new ServletRegistrationBean(new FirstServlet());
        bean.addUrlMappings("/second");
        return bean;
    }

    /**
     * 整合Filter方式二
     * 注册Filter
     */
    @Bean
    public FilterRegistrationBean getFilterRegistrationBean() {
        FilterRegistrationBean bean = new FilterRegistrationBean(new FirstFilter());
        //bean.addUrlPatterns(new String[]{"*.do","*.jsp"});
        bean.addUrlPatterns("/second");
        return bean;
    }

    /**
     * 注册listener
     */
    @Bean
    public ServletListenerRegistrationBean<FirstListener> getServletListenerRegistrationBean() {
        ServletListenerRegistrationBean<FirstListener> bean = new ServletListenerRegistrationBean<>(new FirstListener());
        return bean;
    }
}
