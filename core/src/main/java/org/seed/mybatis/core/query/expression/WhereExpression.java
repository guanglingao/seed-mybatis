package org.seed.mybatis.core.query.expression;


import org.seed.mybatis.core.SqlConsts;

/**
 * 拼接SQL语句
 */
public class WhereExpression implements ExpressionWhereFeature {

    private String joint = SqlConsts.AND;
    private String sql;
    private int index = DEFAULT_INDEX;

    public WhereExpression(String sql) {
        this.sql = sql;
    }

    public WhereExpression(String joint, String sql) {
        this.joint = joint;
        this.sql = sql;
    }

    @Override
    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    @Override
    public String getJoint() {
        return joint;
    }

    public void setJoint(String joint) {
        this.joint = joint;
    }

    @Override
    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public int index() {
        return index;
    }

    @Override
    public ExpressionType expressionType() {
        return ExpressionType.SQL;
    }
}
