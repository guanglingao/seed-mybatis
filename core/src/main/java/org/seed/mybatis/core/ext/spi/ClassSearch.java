package org.seed.mybatis.core.ext.spi;

import java.util.Set;

/**
 * 扫描class
 */
public interface ClassSearch {

    /**
     * 查询class
     *
     * @param targetClass 目标class
     * @param packageName package路径
     * @return 返回class集合
     */
    Set<Class<?>> search(Class<?> targetClass, String... packageName) throws Exception;


    default boolean match(Class<?> clazz) {
        return true;
    }

}
