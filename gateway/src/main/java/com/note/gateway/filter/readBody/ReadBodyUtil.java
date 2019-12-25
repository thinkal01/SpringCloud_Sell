package com.note.gateway.filter.readBody;

public class ReadBodyUtil {
    private static ThreadLocal<Integer> tl = new ThreadLocal<>();

    public static Integer get() {
        if (tl.get() == null) {
            tl.set(1);
        }
        return tl.get();
    }

    public static void set(Integer count) {
        tl.set(count);
    }

    public static void remove() {
        tl.remove();
    }
}
