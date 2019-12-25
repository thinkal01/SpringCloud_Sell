package com.imooc.zuul.consistent;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.*;

@Slf4j
@Component
@RefreshScope
public class ConsistentHashFilter extends ZuulFilter {
    @Autowired
    private ConsistentHashClient consistentHashClient;

    @Value("${consistent.hash.maxRequestContentLength:20000}")
    private long maxRequestContentLength;

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return FilterConstants.PRE_DECORATION_FILTER_ORDER + 2;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            HttpServletRequest request = ctx.getRequest();
            log.info("进入一致性hash过滤器，请求地址为{}", request.getRequestURI());
            String serviceId = (String) ctx.get(SERVICE_ID_KEY);
            serviceId = null;

            // 服务id
            String hashKey = consistentHashClient.getHashKey(serviceId);
            if (StringUtils.isBlank(hashKey)) {
                log.info("非一致性hash处理service,采用默认转发规则");
                return null;
            }

            // 获取hash分发值
            String hashValue = getHashValue(request, hashKey);
            if (StringUtils.isBlank(hashValue)) {
                log.warn("一致性hash分发{}值为空,采用默认转发规则", hashKey);
                return null;
            }

            // 一致性hash所得服务主机
            String host = consistentHashClient.getServer(serviceId, hashValue);
            if (StringUtils.isBlank(host)) {
                log.warn("一致性hash所得服务主机为空,可能由于修改配置hash服务列表所致,采用默认转发规则");
                return null;
            }

            URL requestUrl = new URL("http://" + host + ctx.get(REQUEST_URI_KEY));
            log.info("一致性hash分发，hash key为{},hash值为{}，请求地址为{}", hashKey, hashValue, requestUrl);

            RequestContext.getCurrentContext().setRouteHost(requestUrl);
            ctx.addOriginResponseHeader(SERVICE_HEADER, requestUrl.toString());
            // 移除serviceId
            ctx.remove(SERVICE_ID_KEY);
            // 设置请求地址
            ctx.set(REQUEST_URI_KEY, "");
        } catch (Exception e) {
            log.error("一致性hash分发出现异常，兜底采用网关默认分发规则", e);
        }
        return null;
    }


    /**
     * 获取hashValue
     * @param request
     * @param hashKey
     * @return
     */
    private String getHashValue(HttpServletRequest request, String hashKey) throws IOException {
        if (!checkRequest(request)) return null;

        // 请求方法
        String method = request.getMethod();
        String hashValue = null;

        if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            // 获取Post请求体
            String body = getBody(request);
            hashValue = getPostHashValue(body, hashKey);
            log.debug("请求体为{}", body);
        } else if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            log.debug("请求体为{}", request.getQueryString());
            hashValue = request.getParameter(hashKey);
        }

        return hashValue;
    }

    private String getPostHashValue(String body, String hashKey) {
        // 兼容键值对json，post请求
        String regex = "\"" + hashKey + "\"\\s*:?\\s*(\r\n|\n)*\"?([^\"]+)\"?";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(body);
        if (matcher.find()) {
            return matcher.group(2).trim().toLowerCase();
        }
        return "";
    }

    /**
     * 获取请求体
     * @param request
     * @return
     * @throws IOException
     */
    private String getBody(HttpServletRequest request) throws IOException {
        HttpServletRequest requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
        InputStream is = requestWrapper.getInputStream();
        String body = IOUtils.toString(is, Charset.defaultCharset());
        // 关闭文件输入流
        is.close();
        RequestContext.getCurrentContext().setRequest(requestWrapper);
        return body;
    }

    /**
     * 校验请求，判断是否符合进行hash处理
     * @param request
     * @return
     */
    private boolean checkRequest(HttpServletRequest request) {
        String schema = request.getScheme();
        if ((!HTTP_SCHEME.equals(schema) && !HTTPS_SCHEME.equals(schema))) {
            // 只处理 http 请求(包含https)
            return false;
        }

        if (MediaType.MULTIPART_FORM_DATA_VALUE.equals(request.getContentType())) {
            // 如果为文件上传请求,不进行hash
            return false;
        }

        int contentLength = request.getContentLength();
        // 超过处理长度限制，不进行hash
        return contentLength < maxRequestContentLength;
    }
}
