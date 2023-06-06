package com.lpkaifa.lplibrary.base64;

import com.lpkaifa.lplibrary.exception.FileLargeException;
import org.apache.tika.Tika;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Base64;

public class Base64Utils {
	private Base64Utils(){}
	
	// 根据文件路径获取其格式
	private static String getFileType(File file) throws IOException {
		return new Tika().detect(file);
	}
	
	/**
	 * 编码的总方法
	 * 将一个字节数组编码为base64字符串
	 * @param bytes
	 * @return
	 */
	private static String encode(byte[] bytes) {
		return new String(Base64.getEncoder().encode(bytes));
	}
	
	/**
	 * 解码的总方法
	 * 将一个base64字符串解码为字节数组
	 * @param base64
	 * @return
	 */
	private static byte[] decode(String base64) {
		int index = base64.indexOf(",");
		base64 = base64.substring(index+1);
		return Base64.getDecoder().decode(base64);
	}
	
	/**
	 * 将字符串编码为base64
	 * @param str
	 * @return
	 */
	public static String encodeFromString(String str) {
		return encode(str.getBytes());
	}
	
	/**
	 * 将base64解码为字符串
	 * @param base64
	 * @return
	 */
	public static String decodeToString(String base64) {
		return Arrays.toString(decode(base64));
	}
	
	
	public static String encodeFromFile(String src) throws IOException, FileLargeException {
		return encodeFromFile(new File(src));
	}
	
	/**
	 * 将指定文件编码为base64
	 * @param file 指定的文件对象。
	 *             注意文件大小不能超过1.4GB，否则会抛出异常。
	 * @return base64字符串
	 * @throws IOException 文件不存在时，抛出此异常
	 * @throws FileLargeException 文件大于1.4GB时，抛出此异常
	 */
	public static String encodeFromFile(File file) throws IOException, FileLargeException {
		if(!file.exists()) {
			// 文件不存在
			throw new FileNotFoundException("文件 "+file.getPath()+" 不存在！");
		}
		if(file.length()>1503238553) {
			// 文件太大
			throw new FileLargeException(file,file.length());
		}
		// 获取base64前缀
		String type = getFileType(file);
		String base64Prefix = "data:"+type+";base64,";
		// 获取文件的字节数组
		byte[] bytes = Files.readAllBytes(Paths.get(file.getPath()));
		
		return base64Prefix + encode(bytes);
	}
	
	public static void encodeFromLargeFile(String src,String toSrc) throws IOException {
		encodeFromLargeFile(new File(src),new File(toSrc));
	}
	
	public static void encodeFromLargeFile(String src,File toFile) throws IOException {
		encodeFromLargeFile(new File(src),toFile);
	}
	
	public static void encodeFromLargeFile(File file,String toSrc) throws IOException {
		encodeFromLargeFile(file,new File(toSrc));
	}
	
	/**
	 * 当文件很大时，内存可能不够，调用此方法可以解决问题
	 * @param file 需要编码的文件
	 * @param toFile 编码后的base64字符串放置的文件，默认为："原文件名".base64.txt
	 * @throws IOException 文件不存在时，抛出此异常
	 */
	public static void encodeFromLargeFile(File file,File toFile) throws IOException {
		if(!toFile.exists()) {
			toFile.createNewFile();
		}
		// 获取base64前缀
		String type = getFileType(file);
		String base64Prefix = "data:"+type+";base64,";
		
		// 创建缓冲字节输入流，缓冲区为6kb
		BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file),6144);
		byte[] bytes = new byte[6144];
		
		// 创建打印流，将base64输出到指定文件中
		PrintWriter pw = new PrintWriter(toFile);
		pw.write(base64Prefix);
		
		// 读取文件
		int count = 0;
		while((count = bis.read(bytes))>0) {
			pw.write(encode(bytes));
		}
		
		// 关闭流
		pw.close();
		bis.close();
	}
	
	
	public static void encodeFromLargeFile(String src) throws IOException {
		encodeFromLargeFile(new File(src));
	}
	
	public static void encodeFromLargeFile(File file) throws IOException {
		encodeFromLargeFile(file,new File(file.getPath()+".base64.txt"));
	}
	
	public static void decodeToFile(String base64,String src) {
		decodeToFile(base64,new File(src));
	}
	
	/**
	 * 将base64解码为文件
	 * @param base64 base64字符串
	 * @param file 指定文件路径
	 */
	public static void decodeToFile(String base64,File file) {
		try {
			if(!file.exists()) {
				file.createNewFile();
			}
			
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));
			bos.write(decode(base64));
			
			bos.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	
	
}
