package com.hekewangzi.httpProxyServer.httpMessage.startLine;

import org.apache.commons.lang3.StringUtils;

/**
 * 响应行
 * 
 * @author qq
 * 
 */
public class ResponseStartLine extends StartLine {
	/**
	 * 状态码
	 */
	private int status;

	/**
	 * 状态描述
	 */
	private String description;

	public ResponseStartLine(String startLine) {
		if (StringUtils.isNotBlank(startLine)) {
			/*
			 * 响应行
			 */
			String[] startLineArr = startLine.split(" "); // 改用正在匹配多个空格

			// HTTP协议和协议版本
			String[] protocolAndVersion = startLineArr[0].split("/");
			this.setProtocol(protocolAndVersion[0]);

			String[] version = protocolAndVersion[1].split("\\.");
			this.setMasterVersion(Integer.parseInt(version[0]));
			this.setMinorVersion(Integer.parseInt(version[1]));

			// 状态码
			this.setStatus(Integer.parseInt(startLineArr[1]));

			// 状态描述
			String description = "";
			for (int i = 2; i < startLineArr.length; i++) {
				description += startLineArr[i] + " ";
			}
			this.setDescription(description);
		}
	}

	/*
	 * 样例: http/1.1 200 ok
	 */
	@Override
	public String toString() {
		return String.format("%s/%s.%s %s %s", super.protocol, super.masterVersion, super.minorVersion, this.status,
				this.description);
	}

	/*
	 * getter、setter
	 */
	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
