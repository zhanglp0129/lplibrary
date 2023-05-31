# HttpRequest和HttpResponse
## 功能
- 通过Java代码，模拟发出HTTP请求
- 目前只有get请求和post请求
## 使用方式
1. 创建HttpRequest对象
2. 设置请求信息
    - setPath：设置请求路径，只需要一个路径即可，不需要写参数
    - addParam：添加参数，需传入一个键值对
    - setCookies：设置多个cookie，需传入一个字符串，并且注意格式
    - addCookie：添加一个cookie，每次只能添加一个cookie，需传入一个键值对
    - setUserAgent：设置UA
    - setProxy：设置代理
    - setCharset：设置请求参数的编码方式，默认为utf-8
    - setConnectTimeout：设置连接超时限制，单位为毫秒
    - setSocketTimeout：设置整个过程超时限制，单位为毫秒
    - setRequestTimeout：设置请求超时限制，单位为毫秒
3. 发出请求
    - doGet：发出get请求
    - doPost：发出post请求
4. 获取响应HttpResponse
    - getCode：获取响应状态码
    - getContent：获取响应的完整内容
    - getJsonObject：如果响应为JSON格式，则可以返回一个对象，需传入该对象类型的反射
    - getJsonList：如果响应为JSON格式，则可以返回一个对象的集合，需传入该对象类型的反射
## 示例代码

```Java
import com.lpkaifa.lplibrary.http.HttpRequest;
import com.lpkaifa.lplibrary.http.HttpResponse;

public class Main {
	public static void main(String[] args) {
		HttpRequest httpRequest = new HttpRequest();
		httpRequest.setPath("https://www.example.com");
		httpRequest.addParam("name", "ZhangSan");
		httpRequest.addParam("password", "123456");
		httpRequest.setCookies("user=zhangsan; uid=10000000001; status=1");
		// 发出请求
		HttpResponse httpResponse = httpRequest.doGet();
		System.out.println(httpResponse.getContent());
		// 获取JSON对应的对象
		User user = httpResponse.getJsonObject(User.class);
	}
}
```