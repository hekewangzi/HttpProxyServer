package com.hekewangzi.httpProxyServer.proxy;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hekewangzi.httpProxyServer.constants.RequestMethod;
import com.hekewangzi.httpProxyServer.httpMessage.HttpRequestMessage;
import com.hekewangzi.httpProxyServer.httpMessage.HttpResponseMessage;
import com.hekewangzi.httpProxyServer.httpMessage.exception.BuildHttpMessageError;
import com.hekewangzi.httpProxyServer.httpMessage.startLine.RequestStartLine;
import com.hekewangzi.httpProxyServer.threadPools.ThreadPoolManager;
import com.hekewangzi.httpProxyServer.threads.https.ClientTranslateHttpsToServerThread;
import com.hekewangzi.httpProxyServer.threads.https.ServerTranslateHttpsToClientThread;
import com.hekewangzi.httpProxyServer.utils.SocketUtil;

/**
 * 代理类
 * 
 * @author qq
 *
 */
public class Proxy {
	private final static Logger log = LoggerFactory.getLogger(Proxy.class);

	/*
	 * 配置属性
	 */
	/**
	 * 是否支持压缩（默认：支持）
	 * 
	 * 不压缩的话，可对拦截的Http数据流进行修改
	 */
	protected boolean enabledAcceptEncoding = true;

	/**
	 * 是否支持长连接（默认：不支持）
	 */
	protected boolean enabledKeepAlive = false;

	/**
	 * 转发时是否加密请求(默认: 不加密)
	 */
	private boolean encryptRequest = false;

	/**
	 * 转发时是否加密响应(默认: 不加密)
	 */
	private boolean encryptResponse = false;

	/**
	 * 接收响应时是否解密响应(默认: 不解密)
	 */
	private boolean decryptResponse = false;

	/*
	 * 客户端相关
	 */
	/**
	 * 客户端Socket
	 */
	protected Socket clientSocket;

	/**
	 * 客户端输出流
	 */
	protected OutputStream clientOutputStream;

	/*
	 * 服务端相关
	 */
	/**
	 * 服务端Socket
	 */
	protected Socket serverSocket;

	/**
	 * 服务端输入流
	 */
	protected InputStream serverInputStream;

	/**
	 * 服务端输出流
	 */
	protected OutputStream serverOutputStream;

	/*
	 * 其他
	 */
	protected HttpRequestMessage requestMessage;

	/*
	 * constructor
	 */
	private Proxy() {

	}

	public Proxy(Socket clientSocket, Socket serverSocket, HttpRequestMessage requestMessage) {
		this.requestMessage = requestMessage;

		this.clientSocket = clientSocket;

		this.serverSocket = serverSocket;
		try {
			this.clientOutputStream = clientSocket.getOutputStream();

			this.serverInputStream = serverSocket.getInputStream();
			this.serverOutputStream = serverSocket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * 初始化http配置
		 */
		RequestMethod httpRequestMethod = ((RequestStartLine) requestMessage.getStartLine()).getMethod();
		if (RequestMethod.CONNECT != httpRequestMethod) { // 代理http
			this.initHttp();
		}
	}

	/**
	 * 初始化http
	 */
	private void initHttp() {
		// 删除Connectino首部列出的所有首部字段
		String connectionHeader = requestMessage.getHeader("Connection");
		if (connectionHeader != null) {
			String[] connectionHeaderValues = connectionHeader.split(",");
			requestMessage.removeHeaders(connectionHeaderValues);
		}

		if (!enabledKeepAlive) { // 不支持长连接
			requestMessage.addHeader("Connection", "close"); // 一次请求马上关闭连接
		} else {
			requestMessage.addHeader("Connection", requestMessage.getHeader("Proxy-Connection"));
		}

		requestMessage.removeHeader("Proxy-Connection");

		if (!enabledAcceptEncoding) { // 不支持压缩
			requestMessage.removeHeader("Accept-Encoding");
		}

		System.out.println("[修改后请求: ]");
		System.out.print(requestMessage);
		System.out.println("----------");
	}

	/**
	 * http/https代理
	 */
	public void proxy() {
		RequestMethod httpRequestMethod = ((RequestStartLine) requestMessage.getStartLine()).getMethod();
		if (RequestMethod.CONNECT == httpRequestMethod) { // 代理https
			this.proxyHttps();
		} else { // 代理http
			this.proxyHttp();
		}
	}

	/**
	 * 代理Http
	 * 
	 * 1、将客户端的Http数据流输出给服务端
	 * 
	 * 2、将服务端的Http数据流输出给客户端
	 * 
	 * 3、关闭客户端Socket、关闭服务端Socket
	 * 
	 * @param requestMessage
	 */
	public void proxyHttp() {
		/*
		 * 将客户端数据发给服务端
		 */
		if (!SocketUtil.writeSocket(serverOutputStream, requestMessage, this.encryptRequest)) {
			return;
		}

		/*
		 * 将服务端数据转发给客户端
		 */
		HttpResponseMessage responseMessage = null;
		try {
			responseMessage = new HttpResponseMessage(serverInputStream);
		} catch (BuildHttpMessageError e) {
			e.printStackTrace();
			return;
		}
		if (this.decryptResponse) {
			responseMessage.decryptHttpMessage();
		}

		if (!SocketUtil.writeSocket(clientOutputStream, responseMessage, this.encryptResponse)) {
			return;
		}

		// responseMessage.removeHeader("Set-Cookie"); // 有多个Set-Cookie
		// responseMessage.addHeader("Cache-Control", "no-cache");

		/*
		 * 关闭连接
		 */
		SocketUtil.closeSocket(this.clientSocket, this.serverSocket);
	}

	/**
	 * 代理Https
	 * 
	 * 1、响应客户端Web隧道建立成功
	 * 
	 * 2、将客户端的Https数据流输出给服务端（新建一条线程）
	 * 
	 * 3、将服务端的Http数据流输出给客户端（新建一条线程）
	 * 
	 */
	public void proxyHttps() {
		System.out.println("[代理https请求开始...]");

		/*
		 * 将客户端数据转发给服务端
		 */
		Thread clientToServer = new ClientTranslateHttpsToServerThread(clientSocket, serverSocket);
		ThreadPoolManager.execute(clientToServer);

		/*
		 * 将服务端数据转发给客户端
		 */
		Thread serverToClient = new ServerTranslateHttpsToClientThread(clientSocket, serverSocket);
		ThreadPoolManager.execute(serverToClient);
	}

	/*
	 * getter、setter
	 */
	public boolean isEnabledAcceptEncoding() {
		return enabledAcceptEncoding;
	}

	public void setEnabledAcceptEncoding(boolean enabledAcceptEncoding) {
		this.enabledAcceptEncoding = enabledAcceptEncoding;
	}

	public boolean isEnabledKeepAlive() {
		return enabledKeepAlive;
	}

	public void setEnabledKeepAlive(boolean enabledKeepAlive) {
		this.enabledKeepAlive = enabledKeepAlive;
	}

	public boolean isEncryptRequest() {
		return encryptRequest;
	}

	public void setEncryptRequest(boolean encryptRequest) {
		this.encryptRequest = encryptRequest;
	}

	public boolean isEncryptResponse() {
		return encryptResponse;
	}

	public void setEncryptResponse(boolean encryptResponse) {
		this.encryptResponse = encryptResponse;
	}

	public boolean isDecryptResponse() {
		return decryptResponse;
	}

	public void setDecryptResponse(boolean decryptResponse) {
		this.decryptResponse = decryptResponse;
	}

}
