package org.seed.mybatis.core.query.expression;


public interface ExpressionJoinFeature extends Expression {
    /**
     * 返回连接sql
     *
     * @return 返回连接sql
     */
    String getJoinSql();

    @Override
    default void setIndex(int index) {
    }

    @Override
    default int index() {
        return 0;
    }
}
