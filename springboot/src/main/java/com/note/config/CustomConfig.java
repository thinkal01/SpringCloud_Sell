package com.note.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import javax.annotation.PostConstruct;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class CustomConfig {

    @Autowired
    private RequestMappingHandlerAdapter handlerAdapter;

    @PostConstruct
    private void init() {
        ConfigurableWebBindingInitializer initializer = (ConfigurableWebBindingInitializer) handlerAdapter.getWebBindingInitializer();
        GenericConversionService service = (GenericConversionService) initializer.getConversionService();
        // 自定义日期格式转换器
        service.addConverter(new Converter<String, Date>() {
            @Override
            public Date convert(String s) {
                try {
                    return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").parse(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });
    }
}
