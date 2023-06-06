# Base64Utils
## 功能
- 对一个字符串或文件进行base64编码和解码
## 使用
- 字符串
    - 编码：encodeFromString
    - 解码：decodeToString
- 文件
    - 编码：encodeFromFile
        1. 可以传入文件路径或文件对象
        2. 返回一个base64字符串
        3. 文件不能超过1.4GB，否则会抛出异常
    - 解码：decodeToFile
        1. 传入一个base64字符串和文件路径（或文件对象）
        2. 将base64解码后的文件保存在指定路径中
- 大文件
    - 只能编码：encodeFromLargeFile
    - 传入两个文件路径或文件对象
        1. 需要编码原文件
        2. base64编码后保存的txt文件，默认为："原文件名".base64.txt