package com.lpkaifa.lplibrary.md5;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {
	private Md5Utils(){}
	
	/**
	 * 获取一个字符串的md5值
	 * @param str
	 * @return
	 */
	public static String getMd5(String str) {
		byte[] digest = getMd5Byte(str);
		
		// 转化为16进制整数的字符串
		String md5Str = new BigInteger(1, digest).toString(16);
		return md5Str;
	}
	
	/**
	 * 获取Md5的字节数组存储形式
	 * @param str
	 * @return
	 */
	public static byte[] getMd5Byte(String str) {
		byte[] digest;
		try {
			digest = MessageDigest.getInstance("md5").digest(str.getBytes(StandardCharsets.UTF_8));
		} catch (NoSuchAlgorithmException e) {
			digest = null;
			throw new RuntimeException(e);
		}
		return digest;
	}
	
	/**
	 * 比较md5的大小
	 * @param md51
	 * @param md52
	 * @return 如果左边大于右边，返回正数；左边小于右边，返回负数；相等返回0
	 */
	public static int compareMd5(String md51,String md52) {
		return md51.compareTo(md52);
	}
}
