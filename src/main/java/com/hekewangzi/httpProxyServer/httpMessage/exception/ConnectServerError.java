package com.hekewangzi.httpProxyServer.httpMessage.exception;

/**
 * 连接服务器失败 Exception
 * 
 * @author qq
 *
 */
public class ConnectServerError extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3520679469478229841L;

	public ConnectServerError() {
	}

	public ConnectServerError(String message) {
		super(message);
	}

	public ConnectServerError(Throwable t) {
		super(t);
	}

}
