package org.seed.mybatis.core.query.expression.builder.factory;


import org.seed.mybatis.core.query.Joint;
import org.seed.mybatis.core.query.Operator;
import org.seed.mybatis.core.query.expression.Expression;

/**
 * 表达式工厂,负责生成SQL条件表达式
 */
public interface ExpressionFactory {

    /**
     * 构建表达式
     *
     * @param joint      表达式之间的连接符
     * @param columnName 数据库字段名
     * @param operator   操作符
     * @param value      值
     * @return 返回表达式对象
     */
    Expression buildExpression(Joint joint, String columnName, Operator operator, Object value);
}
