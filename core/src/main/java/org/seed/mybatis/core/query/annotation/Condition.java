package org.seed.mybatis.core.query.annotation;


import org.seed.mybatis.core.query.ConditionValueHandler;
import org.seed.mybatis.core.query.Joint;
import org.seed.mybatis.core.query.Operator;

import java.lang.annotation.*;


/**
 * 条件表达式,作用在bean的get方法上
 */
@Documented
@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Condition {
    /**
     * 表达式之间的连接符,AND|OR,默认AND
     *
     * @return 默认AND
     */
    Joint joint() default Joint.AND;

    /**
     * 数据库字段名
     *
     * @return 默认""
     */
    String column() default "";

    /**
     * 连接符
     *
     * @return 返回连接符
     */
    Operator operator() default Operator.nil;

    /**
     * 是否忽略，设置true，@Condition将不起作用
     *
     * @return 返回true，@Condition将不起作用
     */
    boolean ignore() default false;

    /**
     * 是否忽略空字符串，设置true，忽略空字符串的字段
     *
     * @return 返回true，空字符串字段不生成条件
     */
    boolean ignoreEmptyString() default true;

    /**
     * 默认true情况下查询参数检查空白字符会先进行trim操作，如：传递一个` `空格，将被认为是空字符串。
     *
     * @return 返回true，查询字段去掉首尾空格
     */
    boolean emptyStringWithTrim() default true;

    /**
     * 设置忽略的值，如果字段值跟设置的值匹配，则不会生成条件。<br>
     * 比如前端传一个0，表示未选择，这样需要查询出所有数据，此时需要设置ignoreValue="0"。
     * 如果不设置的话，会生成条件where status = 0导致查不到数据。
     *
     * @return 返回忽略的值
     */
    String[] ignoreValue() default {};

    /**
     * 决定WHERE后面表达式顺序，值小的靠左，可设置该值调整WHERE后面的条件顺序。
     *
     * @return 返回顺序值
     */
    int index() default Integer.MAX_VALUE;

    /**
     * @return 值处理器，用来返回条件值
     */
    Class<? extends ConditionValueHandler> handlerClass() default ConditionValueHandler.DefaultConditionValueHandler.class;
}
