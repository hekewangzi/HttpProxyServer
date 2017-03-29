package com.hekewangzi.httpProxyServer.httpMessage;

import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.hekewangzi.httpProxyServer.constants.RequestMethod;
import com.hekewangzi.httpProxyServer.httpMessage.exception.BuildHttpMessageError;
import com.hekewangzi.httpProxyServer.httpMessage.startLine.RequestStartLine;
import com.hekewangzi.httpProxyServer.httpMessage.startLine.StartLine;
import com.hekewangzi.httpProxyServer.utils.PasswordUtils;

/**
 * * Http请求报文
 * 
 * @author qq
 * 
 */
public class HttpRequestMessage extends HttpMessage {
	private final static Logger log = LoggerFactory.getLogger(HttpRequestMessage.class);

	/*
	 * constructor
	 */
	public HttpRequestMessage(InputStream inputStream) throws BuildHttpMessageError {
		super(inputStream);
	}

	@Override
	public StartLine buildStartLien(String startLine) {
		return new RequestStartLine(startLine);
	}

	/**
	 * 是否包含实体
	 * 
	 * true：POST、PUT
	 * 
	 * CONNECT有吗？
	 * 
	 * @return
	 */
	@Override
	public boolean isSupportBody() {
		RequestMethod requestMethod = ((RequestStartLine) super.getStartLine()).getMethod();

		switch (requestMethod) {
		case POST:
		case PUT:
			return true;
		default:
			return false;
		}
	}

	/**
	 * 加密请求报文
	 */
	@Override
	public HttpMessage encryptHttpMessage() {
		/*
		 * 加密请求行(加密请求url即可)
		 */
		StartLine startLine = this.getStartLine();
		if (startLine instanceof RequestStartLine) {
			RequestStartLine requestStartLine = (RequestStartLine) startLine;
			requestStartLine.setUrl(PasswordUtils.base64Encrypt(requestStartLine.getUrl()));

			super.setStartLine(requestStartLine);
		}

		/*
		 * 加密头部
		 */
		if (!super.headerIsEmpty()) {
			Set<Entry<String, String>> entrys = super.getHeaders().entrySet();
			for (Entry<String, String> entry : entrys) {
				super.addHeader(entry.getKey(), PasswordUtils.base64Encrypt(entry.getValue()));
			}
		}

		return this;
	}

	/**
	 * 解密请求报文
	 */
	@Override
	public HttpMessage decryptHttpMessage() {
		/*
		 * 解密请求行
		 */
		StartLine startLine = this.getStartLine();
		if (startLine instanceof RequestStartLine) {
			RequestStartLine requestStartLine = (RequestStartLine) startLine;
			requestStartLine.setUrl(PasswordUtils.base64Decrypt(requestStartLine.getUrl()));

			super.setStartLine(requestStartLine);
		}

		/*
		 * 解密头部
		 */
		if (!super.headerIsEmpty()) {
			Set<Entry<String, String>> entrys = super.getHeaders().entrySet();
			for (Entry<String, String> entry : entrys) {
				super.addHeader(entry.getKey(), PasswordUtils.base64Decrypt(entry.getValue()));
			}
		}
		return this;
	}

}
