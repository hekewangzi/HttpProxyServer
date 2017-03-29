package com.hekewangzi.httpProxyServer.httpMessage.exception;

/**
 * 构建HttpMessage报文失败 Exception
 * 
 * @author qq
 *
 */
public class BuildHttpMessageError extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 8920548046687303458L;

	public BuildHttpMessageError() {
	}

	public BuildHttpMessageError(String message) {
		super(message);
	}

	public BuildHttpMessageError(Throwable t) {
		super(t);
	}

}
