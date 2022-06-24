package org.seed.mybatis.core.query.expression.builder;


import org.seed.mybatis.core.query.Operator;
import org.seed.mybatis.core.query.annotation.Condition;
import org.seed.mybatis.core.query.expression.BetweenValue;
import org.seed.mybatis.core.query.expression.Expression;
import org.seed.mybatis.core.query.expression.Expressions;
import org.seed.mybatis.core.query.expression.builder.factory.ExpressionFactory;
import org.seed.mybatis.core.util.ClassUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 负责构建条件表达式
 */
public class ExpressionBuilder {
    private static final Map<Operator, ExpressionFactory> factoryMap = new HashMap<>();

    static {
        factoryMap.put(Operator.eq, Expressions.VALUE_EXPRESSION_FACTORY);
        factoryMap.put(Operator.notEq, Expressions.VALUE_EXPRESSION_FACTORY);
        factoryMap.put(Operator.gt, Expressions.VALUE_EXPRESSION_FACTORY);
        factoryMap.put(Operator.ge, Expressions.VALUE_EXPRESSION_FACTORY);
        factoryMap.put(Operator.lt, Expressions.VALUE_EXPRESSION_FACTORY);
        factoryMap.put(Operator.le, Expressions.VALUE_EXPRESSION_FACTORY);

        factoryMap.put(Operator.like, Expressions.LIKE_EXPRESSION_FACTORY);
        factoryMap.put(Operator.likeLeft, Expressions.LIKE_LEFT_EXPRESSION_FACTORY);
        factoryMap.put(Operator.likeRight, Expressions.LIKE_RIGHT_EXPRESSION_FACTORY);

        factoryMap.put(Operator.in, Expressions.LIST_EXPRESSION_FACTORY);
        factoryMap.put(Operator.notIn, Expressions.LIST_EXPRESSION_FACTORY);
        factoryMap.put(Operator.between, Expressions.BETWEEN_EXPRESSION_FACTORY);
    }

    public static Expression buildExpression(Condition annotation, String columnName, Object value) {
        if (annotation == null) {
            throw new NullPointerException("Condition不能为null");
        }
        Operator operator = annotation.operator();
        if (operator == Operator.nil) {
            if (ClassUtil.isArrayOrCollection(value)) {
                operator = Operator.in;
            } else if (value instanceof BetweenValue) {
                operator = Operator.between;
            } else {
                operator = Operator.eq;
            }
        }
        ExpressionFactory expressionFactory = factoryMap.get(operator);

        String column = annotation.column();
        if ("".equals(column.trim())) {
            column = columnName;
        }

        Expression expression = expressionFactory.buildExpression(annotation.joint(), column, operator, value);
        expression.setIndex(annotation.index());

        return expression;
    }

}
