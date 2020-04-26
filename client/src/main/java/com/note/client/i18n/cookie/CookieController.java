package com.note.client.i18n.cookie;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Locale;

@RestController
public class CookieController {
    /**
     * 通过Controller修改系统Locale信息
     */
    @GetMapping(value = "/setCookieLocale", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String cookieLocaleResolver(HttpServletRequest request, HttpServletResponse response) {
        LocaleResolver localeResolver = RequestContextUtils.getLocaleResolver(request);
        String locale = request.getParameter("locale");
        localeResolver.setLocale(request, response, parseLocaleValue(locale));
        return "设置Locale成功";
    }

    private Locale parseLocaleValue(String locale) {
        return Locale.SIMPLIFIED_CHINESE;
    }

    /**
     * 查看Locale信息
     */
    @GetMapping(value = "/getCookieLocale", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String cookieLocaleResolver2(HttpServletRequest request, HttpServletResponse response) {
        String clientLocale = "";
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                clientLocale += cookie.getName() + "=" + cookie.getValue() + ",";
            }
        }
        RequestContext requestContext = new RequestContext(request);
        String value = requestContext.getMessage("message.locale");
        return "客户端发送的Cookie有：" + clientLocale + " </br>当前使用的Locale是：" + requestContext.getLocale() + " </br>使用的资源Locale文件是：" + value;
    }
}
