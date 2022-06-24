package org.seed.mybatis.core.query.expression;


public class JoinExpression implements ExpressionJoinFeature {

    private final String joinSql;

    /**
     * 自定义连接语句
     *
     * @param joinSql inner join table1 t1 on t.xx = t1.xx
     */
    public JoinExpression(String joinSql) {
        this.joinSql = joinSql;
    }

    @Override
    public String getJoinSql() {
        return joinSql;
    }

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.JOIN;
    }
}
