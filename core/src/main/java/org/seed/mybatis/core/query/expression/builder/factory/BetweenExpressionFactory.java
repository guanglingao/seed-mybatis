package org.seed.mybatis.core.query.expression.builder.factory;


import org.seed.mybatis.core.query.Joint;
import org.seed.mybatis.core.query.Operator;
import org.seed.mybatis.core.query.expression.BetweenExpression;
import org.seed.mybatis.core.query.expression.Expression;


public class BetweenExpressionFactory implements ExpressionFactory {
    @Override
    public Expression buildExpression(Joint joint, String columnName, Operator operator, Object value) {
        return new BetweenExpression(columnName, value);
    }
}