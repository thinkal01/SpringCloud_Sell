package com.imooc.zuul.filter;

import org.apache.commons.lang.StringUtils;
import org.springframework.web.util.UrlPathHelper;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class MyUrlPathHelper extends UrlPathHelper {

    private Method getRemainingPath;

    public MyUrlPathHelper() {
        try {
            // 获取父类私有方法
            getRemainingPath = UrlPathHelper.class.getDeclaredMethod("getRemainingPath", String.class, String.class, boolean.class);
            getRemainingPath.setAccessible(true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getPathWithinApplication(HttpServletRequest request) {
        String contextPath = getContextPath(request);
        String requestUri = getRequestUri(request);
        try {
            String path = (String) getRemainingPath.invoke(this, requestUri, contextPath, true);
            if (path != null) {
                path = StringUtils.isNotBlank(path) ? path : "/";
                return getZuulPath(path, request);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return requestUri;
    }

    /**
     * 获取zuul请求路径
     *
     * @param path
     * @param request
     * @return
     */
    private String getZuulPath(String path, HttpServletRequest request) {
        // 获取前端header参数
        String asrIVRType = request.getHeader("asrIVRType");
        if (StringUtils.isNotBlank(asrIVRType)) {
            return "/" + asrIVRType + path;
        }
        return path;
    }

}