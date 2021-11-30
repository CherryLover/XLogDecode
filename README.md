# XLog Decode

XLog 的解码服务，可解码不加密、加密两种。解码加密的日志需要更新 XlogFileDecoder 类中的公钥和私钥。

核心代码来自：https://github.com/wustMeiming/XlogDecoder

可以使用源码启动或使用命令打出一个 jar 包执行。

```bash
./gradlew bootJar #打 jar 包，jar 包在 build/libs 下
nohup java -jar build/libs/xlog_decode_1.0.0.jar --server.port=4208 > log.file  2>&1 & #后台启动并保留日志
ps aux | grep xlog_decode | grep -v grep | awk '{print $2}' | xargs kill -9 # 终止服务
```

启动服务后，使用 Postman 进行测试。

[![Run in Postman](https://run.pstmn.io/button.svg)](https://app.getpostman.com/run-collection/f7324c063fd338ae4849?action=collection%2Fimport)