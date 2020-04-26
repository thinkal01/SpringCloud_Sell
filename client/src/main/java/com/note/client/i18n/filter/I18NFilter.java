package com.note.client.i18n.filter;

import com.note.client.i18n.exception.JSONUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Objects;

/**
 * 实现国际化过滤器
 * 通过Filter技术，对web服务器管理的所有web资源：例如Jsp, Servlet, 静态图片文件或静态 html 文件等进行拦截，从而实现一些特殊的功能。
 * 例如实现URL级别的权限访问控制、过滤敏感词汇、压缩响应信息等一些高级功能。
 * 使用Filter的完整流程：Filter对用户请求进行预处理，接着将请求交给Servlet进行处理并生成响应，最后Filter再对服务器响应进行后处理。
 */
@WebFilter(filterName = "响应国际化过滤器", urlPatterns = "/*")
// 标注这是一个过滤器，属性filterName声明过滤器的名称（可选）；属性urlPatterns指定要过滤的URL模式,也可使用属性value来声明(指定要过滤的URL模式是必选属性)
@Order(Ordered.HIGHEST_PRECEDENCE)// 控制加载顺序,Ordered.HIGHEST_PRECEDENCE最高优先级
@Slf4j
public class I18NFilter implements Filter {
    @Autowired
    private Map<String, I18nPropertiesStrategy> i18nPropertiesStrategyMap;

    /**
     * 前端国际化请求头 key
     */
    private static final String LANGUAGE_HEADER = "Language";

    /**
     * 前端请求头 value
     */
    // 中文
    private static final String ZH_CN = "zh-cn";
    // 英文
    private static final String EN_US = "en-us";

    /**
     * 用于保存请求头中的语言参数
     */
    private String language;

    @Override
    public void init(FilterConfig filterConfig) {
        log.warn("国际化过滤器-init...");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) {
        log.info(">>>>>>>>>>请求进入国际化过滤器<<<<<<<<<");
        HttpServletResponse resp = (HttpServletResponse) response;
        HttpServletRequest req = (HttpServletRequest) request;
        // 获取请求头中的语言参数
        language = req.getHeader(LANGUAGE_HEADER);
        if (StringUtils.isNotBlank(language)) {
            handleResponse(request, response, resp, chain);
        } else {
            log.info(">>>>>> 国际化过滤器不做处理 <<<<<<");
            try {
                response.setCharacterEncoding("UTF-8");
                chain.doFilter(request, response);
            } catch (Exception e) {
                log.info("处理国际化返回结果失败", e);
            }
        }
    }

    /**
     * 处理响应
     * 把返回结果使用 properties 文件中的 key 替换即可，如有动态数据用{}填充即可。
     */
    private void handleResponse(ServletRequest request, ServletResponse response, HttpServletResponse resp, FilterChain chain) {
        // 包装响应对象 resp 并缓存响应数据
        ResponseWrapper mResp = new ResponseWrapper(resp);
        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            // 防止出现乱码
            mResp.setCharacterEncoding("UTF-8");
            chain.doFilter(request, mResp);
            // 获取缓存的响应数据
            byte[] bytes = mResp.getBytes();
            // 响应字符串
            String responseStr = new String(bytes, "UTF-8");
            // 将 String 类型响应数据转成 Response 对象
            Response returnResponse = JSONUtils.json2pojo(responseStr, Response.class);
            // meta 对象
            Response.Meta meta = returnResponse.getMeta();
            // 返回信息
            String msg = meta.getMsg();
            if (meta.getIsI18n()) {
                // 返回信息参数
                Object[] msgParams = meta.getMsgParams();
                // 处理国际化
                if (Objects.isNull(msgParams)) {
                    // 直接用 value 替换 key
                    responseStr = responseStr.replace(msg, getI18nVal(msg));
                } else {
                    // 循环用 value 替换 key
                    String[] keys = msg.split("\\{}");
                    for (String key : keys) {
                        responseStr = responseStr.replaceFirst(key, getI18nVal(key));
                    }
                    // 循环处理返回结果参数
                    for (Object param : msgParams) {
                        responseStr = responseStr.replaceFirst("\\{}", param.toString());
                    }
                }
            }
            out.write(responseStr.getBytes());
        } catch (Exception e) {
            log.error("处理国际化返回结果失败", e);
        } finally {
            try {
                assert out != null;
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据properties文件中属性的key获取对应的值
     */
    private String getI18nVal(String langKey) {
        I18nPropertiesStrategy i18nPropertiesStrategy;
        switch (language) {
            case ZH_CN:
                i18nPropertiesStrategy = i18nPropertiesStrategyMap.get("cnI18nPropertiesStrategy");
                break;
            case EN_US:
                i18nPropertiesStrategy = i18nPropertiesStrategyMap.get("enI18nPropertiesStrategy");
                break;
            default:
                i18nPropertiesStrategy = i18nPropertiesStrategyMap.get("cnI18nPropertiesStrategy");
                break;
        }
        String value = i18nPropertiesStrategy.getValue(langKey);
        log.info("I18N Filter ### key = {} ---->  value = {}", langKey, value);
        return value;
    }

    @Override
    public void destroy() {
        log.warn("国际化过滤器-destroy...");
    }
}