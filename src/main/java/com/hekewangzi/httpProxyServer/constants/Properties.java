package com.hekewangzi.httpProxyServer.constants;

/**
 * 配置 enum
 * 
 * @author qq
 * 
 */
public class Properties {
	/*
	 * 客户端相关配置(本地代理)
	 */
	/**
	 * 客户端Socket读取超时时间(单位: 秒)
	 * 
	 */
	public final static int ClientSoceketReadTimeout = 10 * 1000;

	/*
	 * 代理服务器相关配置
	 */
	/**
	 * 服务端监听端口
	 */
	public final static int ListenerPort = 6666;

	/*
	 * 服务器相关配置(各个要访问的服务器)
	 */
	/**
	 * 服务器Socket读取超时时间(单位: 秒)
	 * 
	 */
	public final static int ServerSocketReadTimeout = 30 * 1000;

	/**
	 * 服务器连接超时时间(单位: 秒)
	 * 
	 */
	public final static int ServerConnectTimeout = 10 * 1000;

	/*
	 * 其他
	 */
	/**
	 * 线程池corePoolSize
	 * 
	 */
	public final static int nThreads = 1000;
}
