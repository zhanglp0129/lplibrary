package com.lpkaifa.lplibrary.http;

import com.lpkaifa.lplibrary.tuple.TwoTuple;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.http.Header;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequest {
	private String path;	// 请求路径，不需要写参数，必填
	private Map<String,String[]> params;	// 请求参数
	private String userAgent;	// 浏览器标识
	private Map<String,String> cookies;
	private TwoTuple<String,Integer> proxy;
	private String charset;	// 设置编码方式，默认为utf-8
	private int connectTimeout;	// 连接超时限制，单位为毫秒，默认为3000
	private int socketTimeout;	// 响应超时限制，单位为毫秒，默认为3000
	private int requestTimeout;	// 请求超时限制，单位为毫秒，默认为3000
	
	public HttpRequest() {
		path="";
		params=new HashMap<>();
		userAgent="";
		cookies=new HashMap<>();
		proxy=new TwoTuple<>(null,-1);
		charset="utf-8";
		connectTimeout=3000;
		socketTimeout =3000;
		requestTimeout=3000;
	}
	
	public void setPath(String path) {
		this.path = path;
	}
	
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	// cookie的格式为：键=值; 键=值; 键=值; 键=值
	public void setCookies(String cookie) {
		// 将Cookie字符串按照"; "分割成多个键值对
		String[] parts = cookie.split("; ");
		for (String part : parts) {
			// 查找第一个出现的"="字符，并将其作为键和值的边界
			int index = part.indexOf("=");
			if (index == -1) {
				continue;
			}
			String key = part.substring(0, index);
			String value = part.substring(index + 1);
			
			// 将键值对添加到Map中
			cookies.put(key, value);
		}
	}
	
	public void addCookie(String key,String value) {
		cookies.put(key,value);
	}
	
	public void addParam(String key,String value) {
		String[] valueArray;
		if(params.containsKey(key)) {
			// Map集合中已存在此键，添加值
			valueArray = params.get(key);
			ArrayUtils.add(valueArray,value);
		} else {
			// Map集合中不存在此键，创建键值对
			valueArray = new String[]{value};
		}
		params.put(key,valueArray);
	}
	
	// 设置代理
	public void setProxy(String address,int port) {
		proxy.first=address;
		proxy.second=port;
	}
	public void setProxy(String address) {
		proxy.first=address;
	}
	public void setProxy(int port) {
		proxy.second=port;
	}
	
	public void setCharset(String charset) {
		this.charset=charset;
	}
	
	public void setConnectTimeout(int connectTimeout) {
		this.connectTimeout = connectTimeout;
	}
	
	public void setSocketTimeout(int socketTimeout) {
		this.socketTimeout = socketTimeout;
	}
	
	public void setRequestTimeout(int requestTimeout) {
		this.requestTimeout = requestTimeout;
	}
	
	// 创建常量，以标记方法
	private final int DO_GET = 1;
	private final int DO_POST = 2;
	
	private HttpResponse doRequest(int method) throws  IOException {
		// 设置请求配置
		RequestConfig  requestConfig;
		if(proxy.first==null||"".equals(proxy.first)||proxy.second==-1) {
			requestConfig = RequestConfig.custom()
					// 设置连接超时时间
					.setConnectTimeout(connectTimeout)
					// 设置整个传输超时时间
					.setSocketTimeout(socketTimeout)
					// 设置从连接池获取链接的超时时间
					.setConnectionRequestTimeout(requestTimeout)
					.build();
		} else {
			requestConfig = RequestConfig.custom()
					// 设置连接超时时间
					.setConnectTimeout(connectTimeout)
					// 设置整个传输超时时间
					.setSocketTimeout(socketTimeout)
					// 设置从连接池获取链接的超时时间
					.setConnectionRequestTimeout(requestTimeout)
					// 设置代理
					.setProxy(new HttpHost(proxy.first,proxy.second))
					.build();
		}
		
		// 设置请求头
		List<Header> headers = new ArrayList<>();
		// 设置UA
		if(userAgent!=null&&!"".equals(userAgent)) {
			headers.add(new BasicHeader(HttpHeaders.USER_AGENT,userAgent));
		}
		// 设置cookie
		if(cookies!=null&&!cookies.isEmpty()) {
			headers.add(new BasicHeader("Cookie",cookiesToString(cookies)));
		}
		
		// 创建核心对象
		CloseableHttpClient httpClient = HttpClients.custom()
				.setDefaultRequestConfig(requestConfig)
				.setDefaultHeaders(headers)
				.build();
		
		if(method==DO_GET) {
			// get请求
			// 创建请求url
			String url = pathParamsToUrl(path,params);
			HttpGet httpGet = new HttpGet(url);
			CloseableHttpResponse response = httpClient.execute(httpGet);
			return new HttpResponse(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity(),"utf-8"));
		} else if(method==DO_POST) {
			// post请求
			HttpPost httpPost = new HttpPost(path);
			// 添加请求参数
			List<NameValuePair> params = new ArrayList<>();
			for(Map.Entry<String,String[]> entry : this.params.entrySet()) {
				String key = entry.getKey();
				String[] values = entry.getValue();
				for (String value : values) {
					params.add(new BasicNameValuePair(key,value));
				}
			}
			httpPost.setEntity(new UrlEncodedFormEntity(params, charset));
			CloseableHttpResponse response = httpClient.execute(httpPost);
			return new HttpResponse(response.getStatusLine().getStatusCode(),EntityUtils.toString(response.getEntity(),"utf-8"));
		}
		
		return new HttpResponse(-1,"");
	}
	
	public HttpResponse doGet() throws IOException {
		return doRequest(DO_GET);
	}
	
	public HttpResponse doPost() throws IOException {
		return doRequest(DO_POST);
	}
	
	// 私有方法，用于将cookies转化为字符串
	private static String cookiesToString(Map<String,String> cookies) {
		StringBuilder stringBuilder = new StringBuilder();
		for (Map.Entry<String, String> entry : cookies.entrySet()) {
			stringBuilder.append(entry.getKey()).append("=").append(entry.getValue()).append("; ");
		}
		return stringBuilder.toString().trim();
	}
	
	// 私有方法
	// 对于get请求，将path和params转换为url
	private static String pathParamsToUrl(String path,Map<String,String[]> params) {
		// 创建StringBuilder对象以构建URL
		StringBuilder builder = new StringBuilder(path);
		// 检查是否存在参数，如果有则将其追加到URL中
		if (params != null && !params.isEmpty()) {
			builder.append("?");
			for (Map.Entry<String, String[]> entry : params.entrySet()) {
				String key = entry.getKey();
				String[] values = entry.getValue();
				
				for (String value : values) {
					try {
						// 对参数值进行URL编码，以避免中文乱码问题
						String encodedValue = URLEncoder.encode(value, StandardCharsets.UTF_8);
						builder.append(key).append("=").append(encodedValue).append("&");
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			// 删除最后一个"&"字符
			builder.deleteCharAt(builder.length() - 1);
		}
		// 使用URI类创建一个具有正确格式的URL字符串，同时对URL中的非法字符进行验证和替换
		URI uri = URI.create(builder.toString());
		return uri.toASCIIString();
	}
}
