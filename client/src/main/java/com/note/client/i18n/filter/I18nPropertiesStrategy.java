package com.note.client.i18n.filter;

/**
 * 国际化属性文件策略
 * 需要新增一种语言，只需新建一个class，实现 I18nPropertiesStrategy 接口。
 */
public interface I18nPropertiesStrategy {
    String getValue(String key);
}