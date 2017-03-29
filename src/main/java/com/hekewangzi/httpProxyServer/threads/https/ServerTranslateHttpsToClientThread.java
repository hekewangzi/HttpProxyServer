package com.hekewangzi.httpProxyServer.threads.https;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketTimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hekewangzi.httpProxyServer.utils.SocketUtil;

/**
 * 处理服务端转发Https数据给客户端线程类
 * 
 * 将服务端的Https数据流输出给客户端
 * 
 * @author qq
 * 
 */
public class ServerTranslateHttpsToClientThread extends Thread {
	private final static Logger log = LoggerFactory.getLogger(ServerTranslateHttpsToClientThread.class);
	/*
	 * Socket
	 */
	/**
	 * 客户端Socket
	 */
	private Socket clientSocket;

	/**
	 * 服务端Socket
	 */
	private Socket serverSocket;

	/*
	 * constructor
	 */
	private ServerTranslateHttpsToClientThread() {
	}

	public ServerTranslateHttpsToClientThread(Socket clientSocket, Socket serverSocket) {
		super();
		this.clientSocket = clientSocket;
		this.serverSocket = serverSocket;
	}

	@Override
	public void run() {
		/*
		 * 
		 */
		InputStream serverInputStream = null; // 服务端输入流
		OutputStream clientOutputStream = null; // 客户端输出流
		try {
			clientOutputStream = clientSocket.getOutputStream();
			serverInputStream = serverSocket.getInputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}

		/*
		 * 
		 */
		int readByte = -1;

		try {
			while ((readByte = serverInputStream.read()) != -1) {
				clientOutputStream.write(readByte);
			}
		} catch (SocketTimeoutException e) { // 读取Socket超时
			e.printStackTrace();
			SocketUtil.closeSocket(clientSocket, serverSocket);
		} catch (IOException e) {
			e.printStackTrace();
			SocketUtil.closeSocket(clientSocket, serverSocket);
		}
	}
}
