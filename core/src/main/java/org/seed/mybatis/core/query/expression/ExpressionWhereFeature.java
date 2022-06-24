package org.seed.mybatis.core.query.expression;


public interface ExpressionWhereFeature extends Expression {
    /**
     * 返回连接符
     *
     * @return 返回连接符
     */
    String getJoint();

    /**
     * 返回sql
     *
     * @return 返回sql
     */
    String getSql();
}
