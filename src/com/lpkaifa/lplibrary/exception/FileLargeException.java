package com.lpkaifa.lplibrary.exception;

import java.io.File;

/**
 * 文件太大异常
 */
public class FileLargeException extends Exception {
	private String message;	// 异常信息
	
	
	/**
	 *
	 * @param src 文件路径
	 * @param limit 最大的限制
	 * @param unit 单位，不写默认为B
	 */
	public FileLargeException(String src, double limit, String unit) {
		super("文件 "+src+" 太大，大于"+limit+unit.toUpperCase());
		this.message = "文件 "+src+" 太大，大于"+limit+unit.toUpperCase();
	}
	
	public FileLargeException(String src, long limit) {
		this(src,limit,"b");
	}
	
	public FileLargeException(File file, long limit, String unit) {
		this(file.getPath(),limit,unit);
	}
	
	public FileLargeException(File file, long limit) {
		this(file,limit,"b");
	}
}
