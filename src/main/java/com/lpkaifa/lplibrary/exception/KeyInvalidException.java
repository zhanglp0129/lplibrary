package com.lpkaifa.lplibrary.exception;

/**
 * 密钥无效异常
 */
public class KeyInvalidException extends RuntimeException {
	private String message;
	
	public KeyInvalidException() {
		super("密钥无效！");
		this.message="密钥无效！";
	}
	
	public KeyInvalidException(String message) {
		super(message);
		this.message = message;
	}
}
