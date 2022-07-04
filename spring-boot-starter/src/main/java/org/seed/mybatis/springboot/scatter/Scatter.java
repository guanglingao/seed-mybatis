package org.seed.mybatis.springboot.scatter;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * 分库、分表注解
 *
 * 结合AbstractRoutingDataSource使用
 * @see org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scatter {

    /**
     * 分库分表字段
     *
     * @return
     */
    @AliasFor("value")
    String by() default "";

    @AliasFor("by")
    String value() default "";

    /**
     * 分库分表策略
     *
     * @return
     */
    Class<? extends ShardingStrategy> strategy() default VoidShardingStrategy.class;

}
