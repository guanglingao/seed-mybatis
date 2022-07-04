package org.seed.mybatis.springboot.scatter;


/**
 * RoutingKey 存储上下文
 */
public class RoutingDataSourceContext {


    /**
     * DataSource
     */
    private static final ThreadLocal<String> dataSourceKey = new InheritableThreadLocal<>();


    public static void setDataSourceKey(String key) {
        dataSourceKey.set(key);
    }

    public static String getDataSourceKey() {
        String key = dataSourceKey.get();
        return key == null ? "default" : key;
    }

    public static void clearDataSourceKey() {
        dataSourceKey.remove();
    }


    /**
     * Table Prefix
     */
    private static final ThreadLocal<String> tablePrefix = new InheritableThreadLocal<>();


    public static void setTablePrefix(String prefix) {
        tablePrefix.set(prefix);
    }

    public static String getTablePrefix() {
        String prefix = tablePrefix.get();
        return prefix == null ? "" : prefix;
    }

    public static void clearTablePrefix() {
        tablePrefix.remove();
    }


    /**
     * Table suffix
     */
    private static final ThreadLocal<String> tableSuffix = new InheritableThreadLocal<>();


    public static void setTableSuffix(String suffix) {
        tableSuffix.set(suffix);
    }

    public static String getTableSuffix() {
        String suffix = tableSuffix.get();
        return suffix == null ? "" : suffix;
    }

    public static void clearTableSuffix() {
        tableSuffix.remove();
    }

}
