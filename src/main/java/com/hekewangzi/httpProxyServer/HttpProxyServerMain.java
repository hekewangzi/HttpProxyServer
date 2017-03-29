package com.hekewangzi.httpProxyServer;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import com.hekewangzi.httpProxyServer.constants.Properties;
import com.hekewangzi.httpProxyServer.threadPools.ThreadPoolManager;
import com.hekewangzi.httpProxyServer.threads.HttpProxyThread;

/**
 * Http代理服务器入口类
 * 
 * @author qq
 * 
 */
public class HttpProxyServerMain {
	private final static Logger log = LoggerFactory.getLogger(HttpProxyServerMain.class);

	/**
	 * 服务端Socket
	 */
	private static ServerSocket serverSocket;

	/**
	 * 程序入口
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		new HttpProxyServerMain().init();

		try {
			while (true) {
				Socket clientSocket = serverSocket.accept();
				// 每当客户端连接后启动一条线程为该客户端服务
				ThreadPoolManager.execute(new HttpProxyThread(clientSocket));
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 系统初始化
	 */
	private void init() {
		log.info("系统初始化开始...");
		this.initLog4j();
		this.initServerSocket();
		log.info("系统初始化完成.");
	}

	/**
	 * 初始化日志
	 */
	private void initLog4j() {
		log.info("日志初始化开始...");
		// 使用SLF4J不需要使用如下配置,否则会日志输出两次
		// BasicConfigurator.configure();
		SLF4JBridgeHandler.install(); // 日志桥接
		log.info("日志初始化结束...");
	}

	/**
	 * 初始化ServerSocket
	 * 
	 * @throws IOException
	 */
	private void initServerSocket() {
		log.info("ServerSocket初始化开始...");

		try {
			serverSocket = new ServerSocket(Properties.ListenerPort);

			log.info("绑定IP：{}", serverSocket.getInetAddress().getHostAddress());
			log.info("绑定端口：{}", serverSocket.getLocalPort());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(0);
		}

		log.info("ServerSocket初始化结束.");
	}

}
