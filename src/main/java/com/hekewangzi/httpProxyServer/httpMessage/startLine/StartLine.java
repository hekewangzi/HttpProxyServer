package com.hekewangzi.httpProxyServer.httpMessage.startLine;

/**
 * 抽象起始行
 * 
 * @author qq
 * 
 */
public abstract class StartLine {
	/**
	 * 使用协议
	 */
	protected String protocol;

	/**
	 * 协议主版本号
	 */
	protected int masterVersion;

	/**
	 * 协议次版本号
	 */
	protected int minorVersion;

	/*
	 * getter、setter
	 */
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public int getMasterVersion() {
		return masterVersion;
	}

	public void setMasterVersion(int masterVersion) {
		this.masterVersion = masterVersion;
	}

	public int getMinorVersion() {
		return minorVersion;
	}

	public void setMinorVersion(int minorVersion) {
		this.minorVersion = minorVersion;
	}

}
