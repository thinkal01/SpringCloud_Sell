package com.note.client.i18n.manage;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.LocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * SpringBoot默认的Locale解析器是根据请求头的区域信息进行解析的
 * 使用自定义的Locale解析器对url的区域信息进行解析达到点击切换区域效果
 * 一旦自定义的区域解析器注册到Spring容器中，则SpringBoot提供的将不自动注册
 */
// language=zh_CN
public class ManageLocaleResolver implements LocaleResolver {
    @Override
    public Locale resolveLocale(HttpServletRequest httpServletRequest) {
        String language = httpServletRequest.getParameter("language");
        if (StringUtils.isEmpty(language)) {
            Locale locale = Locale.getDefault();
            return locale;
        } else {
            String[] split = language.split("_");
            return new Locale(split[0], split[1]);
        }
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
    }
}