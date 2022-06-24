package org.seed.mybatis.core.annotation;

/**
 * 主键策略
 */
public enum KeyStrategy {

    /**
     * 自增
     */
    AUTO,

    /**
     * uuid
     */
    UUID,

    /**
     * 无策略，手动指定主键值
     */
    NONE,

}
