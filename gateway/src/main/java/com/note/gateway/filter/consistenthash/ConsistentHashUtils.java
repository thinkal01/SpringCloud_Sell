package com.note.gateway.filter.consistenthash;

public class ConsistentHashUtils {
    public static final String HASH_VALUE = qualify("hashValue");
    // 开始过滤时间
    public static final String FILTER_START_TIME = qualify("filterStartTime");
    // 开始请求时间
    public static final String REQUEST_START_TIME = "requestStartTime";

    private static String qualify(String attr) {
        return ConsistentHashUtils.class.getName() + "." + attr;
    }
}
