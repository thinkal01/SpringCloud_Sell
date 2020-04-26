package com.note.client.i18n.cookie;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

/**
 * 基于Cookie的国际化实现
 */
public class LanguageCookieInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        String language = request.getParameter("language");
        if (language != null && language.equals("zh")) {
            Locale locale = new Locale("zh", "CN");
            new CookieLocaleResolver().setLocale(request, response, locale);
            request.setAttribute("language", language);
        } else if (language != null && language.equals("en")) {
            Locale locale = new Locale("en", "US");
            new CookieLocaleResolver().setLocale(request, response, locale);
            request.setAttribute("language", language);
        } else {
            new CookieLocaleResolver().setLocale(request, response,
                    LocaleContextHolder.getLocale());
            language = LocaleContextHolder.getLocale().getLanguage();
            request.setAttribute("language", language);
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
    }

}
