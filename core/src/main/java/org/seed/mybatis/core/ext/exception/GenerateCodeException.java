package org.seed.mybatis.core.ext.exception;


public class GenerateCodeException extends RuntimeException {

	private static final long serialVersionUID = -4816235466023240667L;

	public GenerateCodeException(Throwable cause) {
		super(cause);
	}

	public GenerateCodeException(String message) {
		super(message);
	}
	
}
