package org.seed.mybatis.core.query.expression;


public enum ExpressionType {
    JOIN(-1),
    VALUE(0),
    IN(1),
    BETWEEN(2),
    SQL(3),
    ;

    private final int type;

    ExpressionType(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
