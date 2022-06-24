package org.seed.mybatis.core.query.expression.builder.factory;


public class LikeLeftExpressionFactory extends LikeExpressionFactory {

	@Override
	protected String getValue(Object value) {
		return "%" + value;
	}

}
