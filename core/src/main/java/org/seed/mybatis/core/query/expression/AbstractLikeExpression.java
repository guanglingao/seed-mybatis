package org.seed.mybatis.core.query.expression;


import org.seed.mybatis.core.SqlConsts;


public abstract class AbstractLikeExpression extends ValueExpression {

	public AbstractLikeExpression(String column, Object value) {
		super(column, value);
	}

	public AbstractLikeExpression(String joint, String column, Object value) {
		super(joint, column, SqlConsts.LIKE, value);
	}

	@Override
	public String getEqual() {
		return SqlConsts.LIKE;
	}

}
