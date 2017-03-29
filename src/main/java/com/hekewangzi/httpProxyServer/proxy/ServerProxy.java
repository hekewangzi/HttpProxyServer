package com.hekewangzi.httpProxyServer.proxy;

import java.net.Socket;

import com.hekewangzi.httpProxyServer.constants.HttpResponseStatus;
import com.hekewangzi.httpProxyServer.httpMessage.HttpRequestMessage;
import com.hekewangzi.httpProxyServer.httpMessage.HttpResponseMessage;
import com.hekewangzi.httpProxyServer.utils.SocketUtil;

/**
 * 代理服务端
 * 
 * @author qq
 *
 */
public class ServerProxy extends Proxy {

	public ServerProxy(Socket clientSocket, Socket serverSocket, HttpRequestMessage requestMessage) {
		super(clientSocket, serverSocket, requestMessage);
	}

	@Override
	public void proxyHttps() {
		/*
		 * 响应客户端Web隧道建立成功
		 */
		HttpResponseMessage httpResponseMessage = new HttpResponseMessage(HttpResponseStatus._200);
		httpResponseMessage.addHeader("Connection", "close");
		SocketUtil.writeSocket(super.clientOutputStream, httpResponseMessage, super.isEncryptResponse());

		super.proxyHttps();
	}

}
