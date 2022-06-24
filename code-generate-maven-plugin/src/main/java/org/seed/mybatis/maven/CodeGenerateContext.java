package org.seed.mybatis.maven;

public class CodeGenerateContext {

    private static final ThreadLocal<Boolean> withApi = new ThreadLocal<>();

    public static void setSingleModuleMode() {
        withApi.set(false);
    }

    public static void setApiModuleMode() {
        withApi.set(true);
    }

    public static boolean isApiMode() {
        return withApi.get();
    }
}
