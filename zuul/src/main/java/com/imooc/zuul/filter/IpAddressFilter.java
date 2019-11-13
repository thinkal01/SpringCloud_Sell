package com.imooc.zuul.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import java.util.Set;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

public class IpAddressFilter extends ZuulFilter {
    @Override
    public String filterType() {
        // pre类型的过滤器
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        // 排序
        return 1;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        String ip = ctx.getRequest().getRemoteAddr();
        Set<String> blackList = null;
        Set<String> whiteList = null;
        blackList.removeAll(whiteList);

        // 在黑名单中禁用
        if (StringUtils.isNotBlank(ip) && blackList.contains(ip)) {
            ctx.setSendZuulResponse(false);
            ctx.setResponseBody("Suspected flooding attack, IP blocked");
            ctx.setResponseStatusCode(HttpStatus.FORBIDDEN.value());
            ctx.addZuulResponseHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_UTF8_VALUE);
        }

        return null;
    }
}