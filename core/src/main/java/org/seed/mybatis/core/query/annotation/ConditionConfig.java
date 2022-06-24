package org.seed.mybatis.core.query.annotation;

import java.lang.annotation.*;

/**
 * 条件表达式,作用在bean上
 */
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface ConditionConfig {

    /**
     * @return 类中的字段是否驼峰转下划线形式
     */
    boolean camel2underline() default true;

    /**
     * @return 忽略的字段，填类字段名
     */
    String[] ignoreFields() default {};
}
