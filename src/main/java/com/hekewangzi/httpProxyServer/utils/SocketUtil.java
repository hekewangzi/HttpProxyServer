package com.hekewangzi.httpProxyServer.utils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hekewangzi.httpProxyServer.httpMessage.HttpMessage;
import com.hekewangzi.httpProxyServer.httpMessage.exception.ConnectServerError;

/**
 * Socket 工具类
 * 
 * @author qq
 *
 */
public class SocketUtil {
	private final static Logger log = LoggerFactory.getLogger(SocketUtil.class);

	/**
	 * 连接服务器
	 * 
	 * @param host
	 *            服务器主机
	 * @param port
	 *            服务器端口
	 * @param connectTimeout
	 *            连接服务器超时时长
	 * @return 连接成功,返回服务端Socket
	 * @throws ConnectServerError
	 */
	public static Socket connectServer(String host, int port, int connectTimeout) throws ConnectServerError {
		Socket serverSocket = new Socket();
		SocketAddress endpoint;
		try {
			endpoint = new InetSocketAddress(InetAddress.getByName(host), port);
			serverSocket.connect(endpoint, connectTimeout);
		} catch (UnknownHostException e) {
			e.printStackTrace();
			throw new ConnectServerError("Connect Server UnknownHostException");
		} catch (SocketTimeoutException e) { // 连接超时,以后考虑重连
			e.printStackTrace();
			throw new ConnectServerError("Connect Server Is Timeout.");
		} catch (ConnectException e) { // 连接失败
			e.printStackTrace();
			throw new ConnectServerError("Connect Server ConnectionException");
		} catch (IOException e) {
			e.printStackTrace();
			throw new ConnectServerError("Connect Server IOException");
		}
		return serverSocket;
	}

	/**
	 * 写Socket
	 * 
	 * @param outputStream
	 * @param socketStr
	 * @return
	 */
	public static boolean writeSocket(OutputStream outputStream, String socketStr) {
		boolean result = false;
		try {
			outputStream.write(socketStr.getBytes("ISO-8859-1"));
			result = true;
		} catch (IOException e) {
			log.error("writeSocket IOException", e);
		}

		return result;
	}

	/**
	 * 写Socket
	 * 
	 * @param outputStream
	 * @param httpMessage
	 * @param encrypt
	 *            是否加密(true: 加密)
	 * @return
	 */
	public static boolean writeSocket(OutputStream outputStream, HttpMessage httpMessage, boolean encrypt) {
		if (encrypt) {
			httpMessage.encryptHttpMessage();
		}
		return writeSocket(outputStream, httpMessage.toString());
	}

	/**
	 * 关闭Socket
	 * 
	 * @param sockets
	 */
	public static void closeSocket(Socket... sockets) {
		if (sockets != null && sockets.length > 0) {
			int length = sockets.length;
			for (int i = 0; i < length; i++) {
				Socket socket = sockets[i];
				if (socket != null && !socket.isClosed()) {
					try {
						socket.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

}
