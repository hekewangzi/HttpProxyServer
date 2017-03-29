package com.hekewangzi.httpProxyServer.httpMessage.startLine;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import com.hekewangzi.httpProxyServer.constants.RequestMethod;

/**
 * 请求行
 * 
 * @author qq
 * 
 */
public class RequestStartLine extends StartLine {
	/**
	 * 请求方法
	 */
	private RequestMethod method;

	/**
	 * 请求路径
	 */
	private String url;

	/*
	 * constructor
	 */
	public RequestStartLine(String startLine) {
		if (StringUtils.isNotBlank(startLine)) {
			startLine = startLine.trim();
			int firstIndex = startLine.indexOf(" "); // 第一个空格索引
			int lastIndex = startLine.lastIndexOf(" "); // 最后一个空格索引

			// 请求方法
			String requestMethodStr = startLine.substring(0, firstIndex).trim().toUpperCase();
			RequestMethod requestMethod = EnumUtils.getEnum(RequestMethod.class, requestMethodStr);
			this.setMethod(requestMethod);
			// 请求URL
			this.setUrl(startLine.substring(firstIndex, lastIndex).trim());

			// 请求协议和协议版本
			String[] protocolAndVersion = startLine.substring(lastIndex).trim().split("/");
			super.setProtocol(protocolAndVersion[0]);

			String[] version = protocolAndVersion[1].split("\\.");
			super.setMasterVersion(Integer.parseInt(version[0]));
			super.setMinorVersion(Integer.parseInt(version[1]));
		}
	}

	/*
	 * 样例: GET http://c.biancheng.net/cpp/ HTTP/1.1
	 */
	@Override
	public String toString() {
		return String.format("%s %s %s/%s.%s", this.method, this.url, super.protocol, super.masterVersion,
				super.minorVersion);
	}

	/*
	 * getter、setter
	 */
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public RequestMethod getMethod() {
		return method;
	}

	public void setMethod(RequestMethod method) {
		this.method = method;
	}

}
