# lplibrary（亮鹏库）
## 声明
- 动机：对于我来说，市面上的大部分库用起来都比较麻烦，我就想写一个用起来非常方便的库
- 目的：让编程变得更简单，让Java变得更简单
- 该项目将会持续更新，请放心使用
- 版权：该项目为本人原创，转载请说明出处（[github项目首页](https://github.com/zhanglp0129/lplibrary)）
## 如何使用
### 常规导入
1. 在releases中下载jar包
2. 在releases中下载该项目的依赖，或者通过Maven导入依赖（[Maven坐标在下方](https://github.com/zhanglp0129/lplibrary#%E4%BE%9D%E8%B5%96)）
3. 将该项目的jar包和依赖都导入到您的项目，即可使用
4. 下载使用文档，并仔细阅读
### Maven导入
1. 在settings.xml中添加镜像
```xml
<mirror>
    <id>lp-nexus</id>
    <name>lpkaifa maven</name>
    <url>http://118.195.211.227:8081/repository/maven-public/</url>
    <mirrorOf>central</mirrorOf>
</mirror>
```
2. 在pom.xml中添加坐标
```xml
<dependency>
    <groupId>com.lpkaifa</groupId>
    <artifactId>lplibrary</artifactId>
    <version>版本号</version>
</dependency>
```
### 建议
- 使用Maven方式导入，原因：
    1. Maven更方便（你下载方便，我上传也方便）
    2. github上的更新频率不及Maven仓库
## 依赖
```xml
<dependency>
    <groupId>com.google.guava</groupId>
    <artifactId>guava</artifactId>
    <version>30.0-jre</version>
</dependency>
<dependency>
    <groupId>org.apache.httpcomponents</groupId>
    <artifactId>httpclient</artifactId>
    <version>4.3.1</version>
</dependency>
<dependency>
    <groupId>org.apache.tika</groupId>
    <artifactId>tika-core</artifactId>
    <version>1.27</version>
</dependency>
<dependency>
    <groupId>com.alibaba</groupId>
    <artifactId>fastjson</artifactId>
    <version>1.2.67_noneautotype2</version>
</dependency>
<dependency>
    <groupId>org.bouncycastle</groupId>
    <artifactId>bcprov-jdk15on</artifactId>
    <version>1.68</version>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-lang3</artifactId>
    <version>3.12.0</version>
</dependency>
```
## 贡献者
- [zhanglp0129](https://github.com/zhanglp0129)
## 联系方式
- 邮箱：lib@lpkaifa.com
- 微信：lpkaifa
- QQ：2167347683
