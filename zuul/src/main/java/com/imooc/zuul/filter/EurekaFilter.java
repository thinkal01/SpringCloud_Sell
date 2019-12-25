package com.imooc.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;

/**
 * 处理访问eureka资源问题
 */
@Slf4j
@Component
public class EurekaFilter extends ZuulFilter {

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 1;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        if (request.getRequestURI().startsWith("/eureka")) {
            String requestUri = (String) ctx.get(REQUEST_URI_KEY);
            if (StringUtils.isNotBlank(requestUri)) {
                ctx.set(REQUEST_URI_KEY, "/eureka" + requestUri);
            }
        }

        return null;
    }
}