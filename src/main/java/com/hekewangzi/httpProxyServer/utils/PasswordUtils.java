package com.hekewangzi.httpProxyServer.utils;

import java.io.UnsupportedEncodingException;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;

/**
 * 加密解密 工具类
 * 
 * @author qq
 * 
 */
public class PasswordUtils {
	/*****************************************************************************************************
	 * base64加密解密
	 *****************************************************************************************************/
	/**
	 * base64加密
	 * 
	 * @param encryptContent
	 *            加密内容
	 * 
	 * @return 加密后的内容
	 */
	public static String base64Encrypt(String encryptContent) {
		Base64 base64 = new Base64();
		try {
			return base64.encodeToString(encryptContent.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("加密失败", e);
		}
	}

	/**
	 * base64解密
	 * 
	 * @param decryptContent
	 *            解密内容
	 * 
	 * @return 解密后的内容
	 */
	public static String base64Decrypt(String decryptContent) {
		byte[] bytes = new Base64().decodeBase64(decryptContent);
		return new String(bytes);
	}

	/*****************************************************************************************************
	 * Hex加密解密
	 *****************************************************************************************************/
	/**
	 * Hex加密
	 * 
	 * @param encryptContent
	 *            加密内容
	 * 
	 * @return 加密后的内容
	 */
	public static String hexEncrypt(String encryptContent) {
		try {
			return Hex.encodeHexString(encryptContent.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("加密失败", e);
		}
	}

	/**
	 * Hex解密
	 * 
	 * @param decryptContent
	 *            解密内容
	 * 
	 * @return 解密后的内容
	 */
	public static String hexDecrypt(String decryptContent) {
		Hex hex = new Hex();
		try {
			byte[] bytes = hex.decode(decryptContent.getBytes());
			return new String(bytes, "UTF-8");
		} catch (DecoderException e) {
			throw new IllegalArgumentException("解密失败", e);
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("解密失败", e);
		}
	}

	/*****************************************************************************************************
	 * MD5加密
	 *****************************************************************************************************/
	/**
	 * MD5加密
	 * 
	 * @param encryptContent
	 *            加密内容
	 * 
	 * @return 加密后的内容
	 */
	public static String md5Encrypt(String encryptContent) {
		try {
			return DigestUtils.md5Hex(encryptContent.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("加密失败", e);
		}
	}

	/*****************************************************************************************************
	 * SHA加密
	 *****************************************************************************************************/
	/**
	 * SHA加密
	 * 
	 * @param encryptContent
	 *            加密内容
	 * 
	 * @return 加密后的内容
	 */
	public static String shaEncrypt(String encryptContent) {
		try {
			return DigestUtils.shaHex(encryptContent.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			throw new IllegalArgumentException("加密失败", e);
		}
	}

	/*****************************************************************************************************
	 * 
	 *****************************************************************************************************/

	public static void main(String[] args) {
		String xx = base64Decrypt(
				"R0VUIGh0dHA6Ly9jLmJpYW5jaGVuZy5uZXQvY3BwLyBIVFRQLzEuMQ0KYWNjZXB0LWxhbmd1YWdlOiB6aC1DTix6aDtxPTAuOA0KY29va2llOiBVTV9kaXN0aW5jdGlkPTE1YWRhMmJmODljNGRhLTAzZThmOWYzY2U4YzdiLTFkM2U2ODUwLTEzYzY4MC0xNWFkYTJiZjg5ZDY3NDsgQ05aWkRBVEEzODc1NDg0PWNuenpfZWlkJTNEMzUzMzAxMzI2LTE0ODk3MTM5NTUtJTI2bnRpbWUlM0QxNDkwMDIzMTU5DQpob3N0OiBjLmJpYW5jaGVuZy5uZXQNCnVwZ3JhZGUtaW5zZWN1cmUtcmVxdWVzdHM6IDENCmNvbm5lY3Rpb246IGNsb3NlDQpjYWNoZS1jb250cm9sOiBtYXgtYWdlPTANCmFjY2VwdC1lbmNvZGluZzogZ3ppcCwgZGVmbGF0ZSwgc2RjaA0KdXNlci1hZ2VudDogTW96aWxsYS81LjAgKE1hY2ludG9zaDsgSW50ZWwgTWFjIE9TIFggMTBfMTFfNikgQXBwbGVXZWJLaXQvNTM3LjM2IChLSFRNTCwgbGlrZSBHZWNrbykgQ2hyb21lLzU2LjAuMjkyNC44NyBTYWZhcmkvNTM3LjM2DQphY2NlcHQ6IHRleHQvaHRtbCxhcHBsaWNhdGlvbi94aHRtbCt4bWwsYXBwbGljYXRpb24veG1sO3E9MC45LGltYWdlL3dlYnAsKi8qO3E9MC44DQoNCg==");
		System.out.print(xx);
	}
}
