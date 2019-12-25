package com.imooc.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_DECORATION_FILTER_ORDER;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

// @Component
public class TokenFilter extends ZuulFilter {

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return PRE_DECORATION_FILTER_ORDER - 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    // 拦截判断服务接口上是否有传递userToken参数
    @Override
    public Object run() {
        // 1.获取上下文
        RequestContext currentContext = RequestContext.getCurrentContext();
        // 2.获取Request
        HttpServletRequest request = currentContext.getRequest();
        // 3.从请求头中获取token
        String userToken = request.getParameter("userToken");
        // 从url参数中获取
        // String userToken = request.getParameter("userToken");
        if (StringUtils.isEmpty(userToken)) {
            // 不会继续执行，网关服务直接响应给客户端
            currentContext.setSendZuulResponse(false);
            currentContext.setResponseBody("userToken is null");
            currentContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }

        // 正常执行
        return null;
    }
}
