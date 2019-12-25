package com.note.gateway.filter.consistenthash;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBufAllocator;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.CharBuffer;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.note.gateway.filter.consistenthash.ConsistentHashUtils.HASH_VALUE;
import static org.springframework.cloud.gateway.filter.LoadBalancerClientFilter.LOAD_BALANCER_CLIENT_FILTER_ORDER;
import static org.springframework.cloud.gateway.support.ServerWebExchangeUtils.GATEWAY_REQUEST_URL_ATTR;

@Slf4j
@Component
@RefreshScope
public class GetHashValueFilter implements GlobalFilter, Ordered {
    @Autowired
    private ConsistentHashClient consistentHashClient;

    @Value("${consistent.hash.maxRequestContentLength:20000}")
    private long maxRequestContentLength;

    @Override
    public int getOrder() {
        return LOAD_BALANCER_CLIENT_FILTER_ORDER - 20;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        log.info("进入GetHashValue过滤器，请求地址为{}，请求头为{}", exchange.getRequest().getURI(), exchange.getRequest().getHeaders());
        try {
            URI url = exchange.getAttribute(GATEWAY_REQUEST_URL_ATTR);
            // 服务id
            String serviceId = url.getHost();
            String hashKey = consistentHashClient.getHashKey(serviceId);
            if (StringUtils.isBlank(hashKey)) {
                log.info("非一致性hash处理service,采用默认转发规则");
                return chain.filter(exchange);
            }

            // 获取hash分发值
            exchange = getHashValueExchange(exchange, hashKey);
            // 测试异常兜底逻辑
            // if (Math.random() > 0.6) {
            //     throw new RuntimeException();
            // }
            if (StringUtils.isBlank((String) exchange.getAttributes().get(HASH_VALUE))) {
                log.warn("一致性hash分发{}值为空", hashKey);
            }
        } catch (Exception e) {
            log.error("获取hashValue出现异常，兜底采用网关默认分发规则", e);
        } finally {
            return chain.filter(exchange);
        }
    }

    /**
     * 获取包含hashvalue的ServerWebExchange
     * @param exchange
     * @param hashKey
     * @return
     */
    public ServerWebExchange getHashValueExchange(ServerWebExchange exchange, String hashKey) throws UnsupportedEncodingException {
        ServerHttpRequest serverHttpRequest = exchange.getRequest();
        if (!checkRequest(serverHttpRequest)) return exchange;

        String method = serverHttpRequest.getMethod().name();
        String hashValue = null;

        if (HttpMethod.POST.name().equalsIgnoreCase(method)) {
            // 获取Post请求体
            String bodyStr = resolveBodyFromRequest(serverHttpRequest);
            log.info("请求体为{}", bodyStr);
            hashValue = getHashValue(bodyStr, hashKey);

            // 下面将请求体再次封装写回到request里，传到下一级，否则，由于请求体已被消费，后续服务将取不到值
            URI uri = serverHttpRequest.getURI();
            ServerHttpRequest request = serverHttpRequest.mutate().uri(uri).build();
            DataBuffer bodyDataBuffer = stringBuffer(bodyStr);
            Flux<DataBuffer> bodyFlux = Flux.just(bodyDataBuffer);
            request = new ServerHttpRequestDecorator(request) {
                @Override
                public Flux<DataBuffer> getBody() {
                    return bodyFlux;
                }
            };
            // 重新生成ServerWebExchange,封装request,传给下一级
            exchange = exchange.mutate().request(request).build();
        } else if (HttpMethod.GET.name().equalsIgnoreCase(method)) {
            log.info("请求体为{}", JSON.toJSONString(exchange.getRequest().getQueryParams()));
            List<String> hashValueList = serverHttpRequest.getQueryParams().get(hashKey);
            if (!CollectionUtils.isEmpty(hashValueList)) {
                hashValue = hashValueList.get(0);
            }
        }

        // 设置hashValue
        exchange.getAttributes().put(HASH_VALUE, hashValue);
        return exchange;
    }

    /**
     * 校验请求，判断是否符合进行hash处理
     * @param serverHttpRequest
     * @return
     */
    private boolean checkRequest(ServerHttpRequest serverHttpRequest) {
        URI requestUri = serverHttpRequest.getURI();
        String schema = requestUri.getScheme();
        if ((!"http".equals(schema) && !"https".equals(schema))) {
            // 只处理 http 请求(包含https)
            return false;
        }

        MediaType contentType = serverHttpRequest.getHeaders().getContentType();
        if (contentType.includes(MediaType.MULTIPART_FORM_DATA)) {
            // 如果为文件上传请求,不进行hash
            return false;
        }

        long contentLength = serverHttpRequest.getHeaders().getContentLength();
        if (contentLength > maxRequestContentLength) {
            // 超过处理长度限制，不进行hash
            return false;
        }

        return true;
    }

    /**
     * 获取hashValue
     * @param bodyStr
     * @param hashKey
     * @return
     */
    private String getHashValue(String bodyStr, String hashKey) {
        // 兼容键值对，json，post请求
        String regex = "\"" + hashKey + "\"\\s*:?\\s*(\r\n|\n)*\"?(.+)\"?";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(bodyStr);
        if (matcher.find()) {
            return matcher.group(2).trim().toLowerCase();
        }
        return "";
    }

    /**
     * 从Flux<DataBuffer>中获取字符串
     * @return 请求体
     */
    private String resolveBodyFromRequest(ServerHttpRequest serverHttpRequest) {
        // 获取请求体
        Flux<DataBuffer> body = serverHttpRequest.getBody();
        StringBuilder builder = new StringBuilder();
        body.subscribe(buffer -> {
            CharBuffer charBuffer = StandardCharsets.UTF_8.decode(buffer.asByteBuffer());
            DataBufferUtils.release(buffer);
            builder.append(charBuffer.toString());
        });
        // 获取request body
        return builder.toString();
    }

    /**
     * 构建DataBuffer
     * @param value
     * @return
     */
    private DataBuffer stringBuffer(String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        NettyDataBufferFactory nettyDataBufferFactory = new NettyDataBufferFactory(ByteBufAllocator.DEFAULT);
        DataBuffer buffer = nettyDataBufferFactory.allocateBuffer(bytes.length);
        buffer.write(bytes);
        return buffer;
    }
}
