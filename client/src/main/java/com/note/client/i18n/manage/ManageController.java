package com.note.client.i18n.manage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContext;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("manage")
@Slf4j
public class ManageController {

    @GetMapping("test")
    public String test() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes.getRequest();
        String language = request.getParameter("language");
        String key = request.getParameter("key");
        String message = getMessage(request, key);
        return message;
    }

    /**
     * 返回国际化的值
     * @param request
     * @param key
     * @return
     */
    public String getMessage(HttpServletRequest request, String key) {
        String value = "";
        try {
            RequestContext requestContext = new RequestContext(request);
            value = requestContext.getMessage(key);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            value = "";
        }
        return value;
    }
}
