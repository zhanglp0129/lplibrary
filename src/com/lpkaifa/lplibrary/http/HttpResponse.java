package com.lpkaifa.lplibrary.http;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class HttpResponse {
	private int code;	//响应状态码
	private String content;	// 响应内容
	
	
	public HttpResponse(int code,String content) {
		this.code=code;
		this.content=content;
	}
	
	// 获取响应状态码
	public int getCode() {
		return code;
	}
	
	// 获取响应内容，无论请求是否成功
	public String getContent() {
		return content;
	}
	
	// 传入响应成功状态码
	// 如果该请求的状态码在不此之中，返回null
	public String getContent(int...codes) {
		for (int code : codes) {
			if (this.code == code) {
				return content;
			}
		}
		return null;
	}
	
	// 调用getMapper方法后，返回响应内容的类
	// 响应内容必须为JSON格式
	public <T> T getJsonObject(Class<T> responseClass) {
		return JSON.parseObject(content,responseClass);
	}
	
	// 返回响应内容的类的集合
	public <T> List<T> getJsonList(Class<T> responseClass) {
		return JSON.parseArray(content,responseClass);
	}
}
