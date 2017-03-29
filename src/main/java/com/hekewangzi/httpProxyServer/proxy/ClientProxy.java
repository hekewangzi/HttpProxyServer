package com.hekewangzi.httpProxyServer.proxy;

import java.net.Socket;

import com.hekewangzi.httpProxyServer.httpMessage.HttpRequestMessage;
import com.hekewangzi.httpProxyServer.utils.SocketUtil;

/**
 * 代理本地客户端
 * 
 * @author qq
 *
 */
public class ClientProxy extends Proxy {
	public ClientProxy(Socket clientSocket, Socket serverSocket, HttpRequestMessage requestMessage) {
		super(clientSocket, serverSocket, requestMessage);
	}

	@Override
	public void proxyHttps() {
		/*
		 * 将客户端数据发给服务端
		 */
		if (!SocketUtil.writeSocket(super.serverOutputStream, requestMessage, super.isEncryptRequest())) {
			return;
		}

		super.proxyHttps();
	}

}
