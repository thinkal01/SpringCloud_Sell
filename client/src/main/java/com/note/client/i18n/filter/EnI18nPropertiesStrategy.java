package com.note.client.i18n.filter;

import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 英文国际化属性文件策略
 */
@Component
public class EnI18nPropertiesStrategy implements I18nPropertiesStrategy {

    private volatile static PropertiesUtils propertiesUtils;

    @Override
    public String getValue(String key) {
        return getPropertiesUtils().getValue(key);
    }

    private PropertiesUtils getPropertiesUtils() {
        if (Objects.isNull(propertiesUtils)) {
            synchronized (EnI18nPropertiesStrategy.class) {
                if (Objects.isNull(propertiesUtils)) {
                    propertiesUtils = PropertiesUtils.init("i18n/language_en_US.properties");
                }
            }
        }
        return propertiesUtils;
    }
}