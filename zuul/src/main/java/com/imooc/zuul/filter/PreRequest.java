package com.imooc.zuul.filter;

import com.imooc.zuul.consistent.BodyReaderHttpServletRequestWrapper;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.FORM_BODY_WRAPPER_FILTER_ORDER;

/**
 * 增加一个前置过滤器
 */
public class PreRequest extends ZuulFilter {
    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FORM_BODY_WRAPPER_FILTER_ORDER + 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext.getCurrentContext().setRequest(new BodyReaderHttpServletRequestWrapper(RequestContext.getCurrentContext().getRequest()));
        return null;
    }

}