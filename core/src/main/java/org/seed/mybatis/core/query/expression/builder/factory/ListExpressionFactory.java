package org.seed.mybatis.core.query.expression.builder.factory;


import org.seed.mybatis.core.query.Joint;
import org.seed.mybatis.core.query.Operator;
import org.seed.mybatis.core.query.expression.Expression;
import org.seed.mybatis.core.query.expression.ListExpression;
import org.seed.mybatis.core.query.expression.ValueConvert;

import java.util.Collection;


public class ListExpressionFactory implements ExpressionFactory {

    @Override
    public Expression buildExpression(Joint joint, String columnName, Operator operator, Object value) {
        Expression expression = null;
        if (value.getClass().isArray()) {
            expression = new ListExpression(joint.getJoint(), columnName, operator.getOperator(), (Object[]) value);
        } else if (value instanceof Collection) {
            expression = new ListExpression(joint.getJoint(), columnName, operator.getOperator(),
                    (Collection<?>) value);
        }
        return expression;
    }
    
    public <T> Expression buildExpression(Joint joint, String column, Operator operator, Collection<T> value,
            ValueConvert<T> valueConvert) {
        return new ListExpression(joint.getJoint(), column, operator.getOperator(), value, valueConvert);
    }
    
}
