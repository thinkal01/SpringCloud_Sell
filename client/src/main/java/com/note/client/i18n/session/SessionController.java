package com.note.client.i18n.session;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
public class SessionController {
    @GetMapping(value = "/getSessionLocale", produces = "text/html;charset=UTF-8")
    @ResponseBody
    public String sessionLocaleResolver(HttpServletRequest request) {
        RequestContext requestContext = new RequestContext(request);
        String value = requestContext.getMessage("999999");
        HttpSession session = request.getSession();
        return "Session中设置的Locale是：" + session.getAttribute("locale") + " </br>当前使用的Locale是：" + requestContext.getLocale() + " </br>使用的资源Locale文件是：messages_" + value + ".properties";
    }
}
