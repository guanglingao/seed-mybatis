package org.seed.mybatis.core.ext.exception;

/**
 * 生成mapper代码异常
 */
public class MapperFileBuildException extends RuntimeException {

	private static final long serialVersionUID = -4816235466023240677L;

	public MapperFileBuildException(Throwable cause) {
		super(cause);
	}

	public MapperFileBuildException(String message) {
		super(message);
	}
	
}
