package com.note.client.i18n.manage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 扩展SpringMVC
 * SpringBoot2使用的Spring5，因此将WebMvcConfigurerAdapter改为WebMvcConfigurer
 * 使用WebMvcConfigurer扩展SpringMVC既保留了SpringBoot的自动配置，又能用到我们的配置
 * @EnableWebMvc 如果需要全面接管SpringBoot中的SpringMVC配置则开启此注解，开启后，SpringMVC的自动配置将会失效。
 */
@Configuration
public class ManageLocaleConfig implements WebMvcConfigurer {
    //注册自定义的区域解析器，默认提供的区域解析器将不会自动注册
    @Bean
    public LocaleResolver localeResolver() {
        return new ManageLocaleResolver();
    }
}
