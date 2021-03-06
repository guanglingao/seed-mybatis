package org.seed.mybatis.core.query.expression;


import org.seed.mybatis.core.query.Joint;
import org.seed.mybatis.core.query.Operator;
import org.seed.mybatis.core.query.expression.builder.factory.*;

import java.util.Collection;

/**
 * 表达式工厂类
 */
public class Expressions {
    private Expressions() {
        super();
    }

    public static final ExpressionFactory VALUE_EXPRESSION_FACTORY = new ValueExpressionFactory();
    public static final ExpressionFactory LIKE_EXPRESSION_FACTORY = new LikeExpressionFactory();
    public static final ExpressionFactory LIKE_LEFT_EXPRESSION_FACTORY = new LikeLeftExpressionFactory();
    public static final ExpressionFactory LIKE_RIGHT_EXPRESSION_FACTORY = new LikeRightExpressionFactory();
    public static final ListExpressionFactory LIST_EXPRESSION_FACTORY = new ListExpressionFactory();
    public static final BetweenExpressionFactory BETWEEN_EXPRESSION_FACTORY = new BetweenExpressionFactory();

    private static final Joint JOINT = Joint.AND;

    public static Expression eq(String columnName, Object value) {
        return VALUE_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.eq, value);
    }

    public static Expression notEq(String columnName, Object value) {
        return VALUE_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.notEq, value);
    }

    public static Expression gt(String columnName, Object value) {
        return VALUE_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.gt, value);
    }

    public static Expression ge(String columnName, Object value) {
        return VALUE_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.ge, value);
    }

    public static Expression lt(String columnName, Object value) {
        return VALUE_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.lt, value);
    }

    public static Expression le(String columnName, Object value) {
        return VALUE_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.le, value);
    }

    public static Expression like(String columnName, String value) {
        return LIKE_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.like, value);
    }

    public static Expression likeLeft(String columnName, String value) {
        return LIKE_LEFT_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.likeLeft, value);
    }

    public static Expression likeRight(String columnName, String value) {
        return LIKE_RIGHT_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.likeRight, value);
    }

    public static <T> Expression in(String columnName, Collection<T> value, ValueConvert<T> valueConvert) {
        return LIST_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.in, value, valueConvert);
    }

    public static Expression in(String columnName, Collection<?> value) {
        return LIST_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.in, value);
    }

    public static Expression in(String columnName, Object[] value) {
        return LIST_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.in, value);
    }

    public static Expression notIn(String columnName, Collection<?> value) {
        return LIST_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.notIn, value);
    }

    public static <T> Expression notIn(String columnName, Collection<T> value, ValueConvert<T> valueConvert) {
        return LIST_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.notIn, value, valueConvert);
    }

    public static Expression notIn(String columnName, Object[] value) {
        return LIST_EXPRESSION_FACTORY.buildExpression(JOINT, columnName, Operator.notIn, value);
    }

    public static Expression between(String columnName, Object value) {
        return BETWEEN_EXPRESSION_FACTORY.buildExpression(null, columnName, null, value);
    }

    public static Expression join(String joinSql) {
        return new JoinExpression(joinSql);
    }

    public static Expression sql(String sql) {
        return new WhereExpression(sql);
    }
}
