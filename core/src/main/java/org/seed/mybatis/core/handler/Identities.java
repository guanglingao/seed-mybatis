package org.seed.mybatis.core.handler;


public class Identities {

    private static ThreadLocal<Object> id = new ThreadLocal<>();

    public static Object get() {
        return id.get();
    }

    public static void set(Object val) {
        id.set(val);
    }

    public static void remove() {
        id.remove();
    }
}
