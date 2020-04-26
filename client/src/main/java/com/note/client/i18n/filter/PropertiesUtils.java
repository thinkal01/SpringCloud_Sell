package com.note.client.i18n.filter;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * 读取Properties文件工具类
 */
@Slf4j
public class PropertiesUtils {
    private String path;
    private Properties properties;

    private PropertiesUtils(String path) {
        this.path = path;
        try {
            this.properties = PropertiesLoaderUtils.loadProperties(new ClassPathResource(path));
        } catch (IOException e) {
            log.error(String.format("地址为 %s 的文件不存在", path), e);
        }
    }

    /**
     * 构建PropertiesUtil
     *
     * @param path 资源文件路径,如:i18n/zh_CN.properties
     */
    public static PropertiesUtils init(String path) {
        return new PropertiesUtils(path);
    }

    /**
     * 获取配置文件中的值
     *
     * @param key 键
     */
    public String getValue(String key) {
        if (StringUtils.isBlank(key)) {
            throw new NullPointerException(String.format("配置文件 %s 中找不到这个Key，key = %s", path, key));
        }
        key = key.trim();
        String property = properties.getProperty(key);
        if (StringUtils.isBlank(property)) {
            throw new NullPointerException(String.format("配置文件 %s 中 key = %s 的 value 为空", path, key));
        }
        return property.trim();
    }
}