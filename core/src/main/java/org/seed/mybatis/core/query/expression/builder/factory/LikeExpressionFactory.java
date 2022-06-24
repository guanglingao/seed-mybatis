package org.seed.mybatis.core.query.expression.builder.factory;


import org.seed.mybatis.core.query.Joint;
import org.seed.mybatis.core.query.Operator;
import org.seed.mybatis.core.query.expression.Expression;
import org.seed.mybatis.core.query.expression.ValueExpression;


public class LikeExpressionFactory implements ExpressionFactory {

    @Override
    public Expression buildExpression(Joint joint, String columnName, Operator operator, Object value) {
        return new ValueExpression(joint.getJoint(), columnName, operator.getOperator(), this.getValue(value));
    }

    protected String getValue(Object value) {
        return "%" + value + "%";
    }

}
