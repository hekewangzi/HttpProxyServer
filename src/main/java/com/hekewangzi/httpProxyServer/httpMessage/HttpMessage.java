package com.hekewangzi.httpProxyServer.httpMessage;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hekewangzi.httpProxyServer.httpMessage.exception.BuildHttpMessageError;
import com.hekewangzi.httpProxyServer.httpMessage.startLine.StartLine;

/**
 * 抽象Http报文
 * 
 * @author qq
 * 
 */
public abstract class HttpMessage {
	private final static Logger log = LoggerFactory.getLogger(HttpMessage.class);

	/**
	 * 起始行
	 */
	private StartLine startLine;

	/**
	 * 首部
	 * 
	 * key全部转为小写
	 */
	private Map<String, String> headers = new HashMap<String, String>();

	/**
	 * 实体
	 */
	private String body;

	/*
	 * constructor
	 */
	public HttpMessage() {

	}

	public HttpMessage(InputStream inputStream) throws BuildHttpMessageError {
		this.buildHttpMessage(inputStream);
	}

	/*
	 * ***********************************************************************
	 * 起始行相关方法
	 * ***********************************************************************
	 */
	/**
	 * 获取起始行
	 * 
	 * @return
	 */
	public StartLine getStartLine() {
		return this.startLine;
	}

	/**
	 * 设置起始行
	 * 
	 * @param startLine
	 */
	public void setStartLine(StartLine startLine) {
		this.startLine = startLine;
	}

	/*
	 * ***********************************************************************
	 * 首部相关方法
	 * ***********************************************************************
	 */
	/**
	 * 添加首部
	 * 
	 * @param key
	 * @param value
	 */
	public void addHeader(String key, String value) {
		if (StringUtils.isNotBlank(key) && StringUtils.isNotBlank(value)) {
			this.headers.put(key.trim().toLowerCase(), value.trim());
		}
	}

	/**
	 * 批量添加首部
	 * 
	 * @param keyValues
	 *            格式：key1,value1,key2,value2
	 */
	public void addHeaders(String... keyValues) {
		if (keyValues != null && keyValues.length >= 2) { // 最少两个参数
			int length = keyValues.length;
			if (length % 2 != 0) { // 奇数个，去掉最后一个
				--length;
			}

			for (int i = 0; i < length; i += 2) {
				this.addHeader(keyValues[i], keyValues[i + 1]);
			}
		}
	}

	/**
	 * 批量添加首部
	 * 
	 * @param headers
	 */
	public void addHeaders(Map<String, String> headers) {
		if (headers != null && !headers.isEmpty()) {
			Set<Entry<String, String>> entrys = headers.entrySet();
			for (Entry<String, String> entry : entrys) {
				this.addHeader(entry.getKey(), entry.getValue());
			}
		}
	}

	/**
	 * 获取首部
	 * 
	 * @param key
	 * @return
	 */
	public String getHeader(String key) {
		return StringUtils.isNotBlank(key) ? headers.get(key.trim().toLowerCase()) : null;
	}

	/**
	 * 获取所有头部
	 * 
	 * @return
	 */
	public Map<String, String> getHeaders() {
		return this.headers;
	}

	/**
	 * 删除首部
	 * 
	 * @param key
	 */
	public void removeHeader(String key) {
		if (StringUtils.isNotBlank(key)) {
			headers.remove(key.trim().toLowerCase());
		}
	}

	/**
	 * 批量删除首部
	 * 
	 * @param keys
	 */
	public void removeHeaders(String... keys) {
		if (keys != null && keys.length > 0) {
			int length = keys.length;
			for (int i = 0; i < length; i++) {
				this.removeHeader(keys[i]);
			}
		}
	}

	/**
	 * 是否有首部
	 * 
	 * @return true:无首部
	 */
	public boolean headerIsEmpty() {
		return headers.isEmpty();
	}

	/*
	 * ***********************************************************************
	 * 实体相关方法
	 * ***********************************************************************
	 */
	/**
	 * 获取实体
	 * 
	 * @return
	 */
	public String getBody() {
		return body;
	}

	/**
	 * 设置实体
	 * 
	 * @param body
	 */
	public void setBody(String body) {
		this.body = body;
	}

	/**
	 * 是否支持实体
	 * 
	 * @return
	 */
	public abstract boolean isSupportBody();

	/*
	 * ***********************************************************************
	 * 解析相关方法
	 * ***********************************************************************
	 */
	/**
	 * 构建起始行
	 * 
	 * @param inputStream
	 * @return 输入流
	 * @throws BuildHttpMessageError
	 * @throws IOException
	 */
	private String buildStartLine(InputStream inputStream) throws BuildHttpMessageError {
		StringBuffer line = new StringBuffer();
		int byteOfData = -1;

		try {
			while ((byteOfData = inputStream.read()) != -1) {
				char readChar = (char) byteOfData;
				System.out.print(readChar);
				if (readChar == '\n') { // 行尾返回
					break;
				} else {
					if (readChar == '\r') {
						continue;
					}
					line.append(readChar);
				}
			}
		} catch (SocketTimeoutException e) { // 读取超时
			e.printStackTrace();
			throw new BuildHttpMessageError("buildStartLine SocketTimeoutException");
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildHttpMessageError("buildStartLine IOException");
		}
		return line.toString().trim();
	}

	/**
	 * 构建StartLine
	 * 
	 * @param startLine
	 * @return
	 */
	public abstract StartLine buildStartLien(String startLine);

	/**
	 * 构建头部
	 * 
	 * @param inputStream
	 *            输入流
	 * @return
	 * @throws BuildHttpMessageError
	 */
	private void buildHeader(InputStream inputStream) throws BuildHttpMessageError {
		StringBuffer header = new StringBuffer(); // 每行的数据
		int byteOfData = -1;

		try {
			while ((byteOfData = inputStream.read()) != -1) {
				char readChar = (char) byteOfData;
				if (readChar == '\n') { // 行尾
					if (header.length() == 0) { // 该行长度为0,返回
						break;
					}

					String headerStr = header.toString();
					int colonIndex = headerStr.indexOf(":");// 不能使用split(":")，因为value中可能包含":"
					this.addHeader(headerStr.substring(0, colonIndex), headerStr.substring(colonIndex + 1));

					header.setLength(0); // 清空line
				} else { // 非行尾
					if (readChar == '\r') {
						continue;
					}
					header.append(readChar);
				}
			}
		} catch (SocketTimeoutException e) { // 读取超时
			e.printStackTrace();
			throw new BuildHttpMessageError("buildHeader SocketTimeoutException");
		} catch (IOException e) {
			e.printStackTrace();
			throw new BuildHttpMessageError("buildHeader IOException");
		}
	}

	/**
	 * 构建实体
	 * 
	 * @param inputStream
	 *            输入流
	 * @return
	 * @throws IOException
	 */
	private void buildBody(InputStream inputStream) {
		StringBuffer line = new StringBuffer();
		int byteOfData = -1;

		try {
			while ((byteOfData = inputStream.read()) != -1) {
				char readChar = (char) byteOfData;
				System.out.print(readChar);
				line.append(readChar);
			}
		} catch (SocketTimeoutException e) { // 读取超时
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		String lineStr = line.toString(); // 不能trim(),trim()之后长度就不一样了,mlgbd
		this.setBody(lineStr);
	}

	/**
	 * 根据输入流构建HttpMessage报文
	 * 
	 * @param inputStream
	 * @throws BuildHttpMessageError
	 */
	public HttpMessage buildHttpMessage(InputStream inputStream) throws BuildHttpMessageError {
		if (inputStream == null) {
			throw new BuildHttpMessageError("inputStream is null");
		}

		/*
		 * 构建起始行
		 */
		String startLineStr = this.buildStartLine(inputStream);
		StartLine startLine = this.buildStartLien(startLineStr);
		this.startLine = startLine;

		if (StringUtils.isBlank(startLineStr) || this.startLine == null) {
			throw new BuildHttpMessageError("startLine is null");
		}

		/*
		 * 构建头部
		 */
		this.buildHeader(inputStream);

		/*
		 * 构建实体
		 */
		if (this.isSupportBody()) {
			this.buildBody(inputStream);
		}

		return this;
	}

	/*
	 * ***********************************************************************
	 * 加密、解密方法
	 * ***********************************************************************
	 */
	/**
	 * 加密报文
	 * 
	 * @return
	 */
	public abstract HttpMessage encryptHttpMessage();

	/**
	 * 解密报文
	 * 
	 * @return
	 */
	public abstract HttpMessage decryptHttpMessage();

	/*
	 * ***********************************************************************
	 * 其他方法
	 * ***********************************************************************
	 */
	/**
	 * 返回Http报文
	 * 
	 * @return 按Http报文格式输出字符串
	 */
	@Override
	public String toString() {
		StringBuffer httpMessage = new StringBuffer();

		if (this.getStartLine() == null) {
			return null;
		}

		/*
		 * 起始行
		 */
		httpMessage.append(this.getStartLine());
		httpMessage.append("\r\n");

		/*
		 * 首部
		 */
		if (!this.headerIsEmpty()) {
			Set<Entry<String, String>> entrys = headers.entrySet();
			for (Entry<String, String> entry : entrys) {
				httpMessage.append(entry.getKey());
				httpMessage.append(": ");
				httpMessage.append(entry.getValue());
				httpMessage.append("\r\n");
			}
		}
		httpMessage.append("\r\n");

		/*
		 * 实体
		 */
		if (StringUtils.isNotBlank(this.body)) {
			httpMessage.append(this.body);
		}

		return httpMessage.toString();
	}

}
