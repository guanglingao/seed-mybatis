package org.seed.mybatis.core.query.expression;

/**
 *
 */
public interface ExpressionBetweenFeature extends Expression {

    /**
     * 返回数据库字段名
     *
     * @return 返回数据库字段名
     */
    String getColumn();

    /**
     * 开始值
     *
     * @return 返回开始值
     */
    Object getStartValue();

    /**
     * 结束值
     *
     * @return 返回结束值
     */
    Object getEndValue();
}
