package com.note.client.i18n.filter;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 中国国际化属性文件策略
 */
@Component
public class CnI18nPropertiesStrategy implements I18nPropertiesStrategy {

    private volatile static PropertiesUtils propertiesUtils;

    @Override
    public String getValue(String key) {
        return getPropertiesUtils().getValue(key);
    }

    private PropertiesUtils getPropertiesUtils() {
        if (Objects.isNull(propertiesUtils)) {
            synchronized (CnI18nPropertiesStrategy.class) {
                if (Objects.isNull(propertiesUtils)) {
                    propertiesUtils = PropertiesUtils.init("i18n/language_zh_CN.properties");
                }
            }
        }
        return propertiesUtils;
    }
}