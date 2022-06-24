package org.seed.mybatis.core.query.expression;

import java.util.List;

/**
 * 查询条件支持
 */
public interface ExpressionFeature {

    /**
     * 添加表达式
     *
     * @param expression 表达式对象
     * @return 返回Expressional对象
     */
    ExpressionFeature addExpression(Expression expression);

    /**
     * 返回连接表达式列表
     *
     * @return 返回连接表达式列表
     */
    List<ExpressionJoinFeature> getJoinExpressions();

}
