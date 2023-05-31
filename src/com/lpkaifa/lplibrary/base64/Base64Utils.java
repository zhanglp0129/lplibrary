package com.lpkaifa.lplibrary.base64;

import com.google.common.primitives.Bytes;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.List;

public class Base64Utils {
	private Base64Utils() {}
	
	// 通过获取字节数组，获取图片的编码方式
	private static String getImageEncodingType(byte[] imageBytes) throws IOException {
		ByteArrayInputStream bais = new ByteArrayInputStream(imageBytes);
		ImageInputStream iis = ImageIO.createImageInputStream(bais);
		ImageReader reader = ImageIO.getImageReaders(iis).next();
		reader.setInput(iis, true);
		String formatName = reader.getFormatName();
		iis.close();
		bais.close();
		return formatName.toLowerCase();
	}
	
	// 将字符串编码为base64
	public static String encodeFromString(String str) {
		return new String(Base64.getEncoder().encode(str.getBytes(StandardCharsets.UTF_8)));
	}
	
	// 将图片编码为base64，通过图片的路径输入
	public static String encodeFromImg(String src) throws IOException {
		// 获取图片的字节数组
		Path path = Paths.get(src);
		byte[] img = Files.readAllBytes(path);
		
		return encodeFromImg(img);
	}
	
	// 将图片编码为base64，通过字节集合输入
	public static String encodeFromImg(List<Byte> img) throws IOException {
		// 将集合转换为数组，使用谷歌的库很方便
		byte[] imgByteArray = Bytes.toArray(img);
		return encodeFromImg(imgByteArray);
	}
	
	// 将图片编码为base64，通过字节数组输入
	public static String encodeFromImg(byte[] img) throws IOException {
		String encodingType = getImageEncodingType(img);
		return "data:image/"+encodingType+";base64," + new String(Base64.getEncoder().encode(img));
	}
	
	// 解码为字符串
	public static String decodeToString(String base64) {
		return new String(Base64.getDecoder().decode(base64));
	}
	
	// 解码为图片的字符数组
	// 不写解码后的存储形式，则默认为：返回直接数组
	public static byte[] decodeToImg(String base64) {
		int index=base64.indexOf(",");
		if(index>0) {
			base64=base64.substring(index+1);
		}
		return Base64.getDecoder().decode(base64);
	}
	
	// 指定路径解码图片
	public static void decodeToImg(String base64,String src) throws IOException {
		File file = new File(src);
		FileOutputStream fos = new FileOutputStream(file);
		byte[] img = decodeToImg(base64);
		fos.write(img);
		
		fos.close();
		
	}
	
	// 指定字节集合解码图片
	public static void decodeToImg(String base64,List<Byte> img) {
		img = Bytes.asList(decodeToImg(base64));
	}
	
	// 指定字节数组解码图片
	public static void decodeToImg(String base64,byte[] img) {
		img=decodeToImg(base64);
	}
}
