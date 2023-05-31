package com.lpkaifa.lplibrary.md5;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Utils {
	private Md5Utils(){}
	
	public static String getMd5(String str) throws NoSuchAlgorithmException {
		byte[] digest = getMd5Byte(str);
		
		// 转化为16进制整数的字符串
		String md5Str = new BigInteger(1, digest).toString(16);
		return md5Str;
	}
	
	// 获取Md5的字节数组存储形式
	public static byte[] getMd5Byte(String str) throws NoSuchAlgorithmException {
		byte[] digest = MessageDigest.getInstance("md5").digest(str.getBytes(StandardCharsets.UTF_8));
		return digest;
	}
	
	// 如果左边大于右边，返回正数；左边小于右边，返回负数；相等返回0
	public static int compareMd5(String md51,String md52) {
		return md51.compareTo(md52);
	}
}
