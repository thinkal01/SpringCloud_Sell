package com.note.gateway.filter;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class RequestTimeGatewayFilterFactory extends AbstractGatewayFilterFactory<RequestTimeGatewayFilterFactory.Config> {
    private static final String REQUEST_TIME_BEGIN = "requestTimeBegin";
    private static final String KEY = "withParams";

    @Override
    public List<String> shortcutFieldOrder() {
        return Arrays.asList(KEY);
    }

    public RequestTimeGatewayFilterFactory() {
        // 构造器中一定要调用下父类的构造器把Config类型传过去，否则会报ClassCastException
        super(Config.class);
    }

    /*
     apply(Config config)方法内创建了一个GatewayFilter的匿名类，
     具体的实现逻辑跟之前一样，只不过加了是否打印请求参数的逻辑，而这个逻辑的开关是config.isWithParams()。
     静态内部类Config就是为了接收那个boolean类型的参数，里边的变量名可以随意写，但是要重写List shortcutFieldOrder()这个方法。
     */
    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            exchange.getAttributes().put(REQUEST_TIME_BEGIN, System.currentTimeMillis());
            return chain.filter(exchange).then(
                    Mono.fromRunnable(() -> {
                        Long startTime = exchange.getAttribute(REQUEST_TIME_BEGIN);
                        if (startTime != null) {
                            StringBuilder sb = new StringBuilder(exchange.getRequest().getURI().getRawPath())
                                    .append(": ")
                                    .append(System.currentTimeMillis() - startTime)
                                    .append("ms");
                            if (config.isWithParams()) {
                                sb.append(" params:").append(exchange.getRequest().getQueryParams());
                            }
                            log.info(sb.toString());
                        }
                    })
            );
        };
    }

    @Getter
    @Setter
    public static class Config {
        private boolean withParams;
    }
}