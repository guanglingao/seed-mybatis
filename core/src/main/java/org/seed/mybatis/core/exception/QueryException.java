package org.seed.mybatis.core.exception;

/**
 * 条件构建异常
 */
public class QueryException extends RuntimeException {

	private static final long serialVersionUID = -4816235466023240549L;


	public QueryException(String message) {
		super(message);
	}

	public QueryException(Throwable cause) {
		super(cause);
	}

}
