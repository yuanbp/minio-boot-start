# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/maven-plugin/)
* [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.2.0.RELEASE/reference/htmlsingle/#configuration-metadata-annotation-processor)

### 添加 POM 引用
```xml
<dependency>
    <groupId>org.chieftain</groupId>
    <artifactId>minio-boot-starter</artifactId>
    <version>${最新版本}</version>
</dependency>
```

配置示例
```yaml
minio:
  url: http://192.168.1.71:9000
  access-key: cea8bb09ec17123c35fed7a62192211e
  secret-key: e2c452158f84afd2ec1fcf62592b66a2
  bucket-name: trading-platform
  system-platform: unix
  pool:
    max-total: 30
    max-idle: 10
    min-idle: 3
    max-wait-millis: 3000
    block-when-exhausted: true
  endpoint:
    enable: true
    name: minioendpoint
```

### 配置属性说明
- url: minio 的服务地址
- access-key: 
- secret-key: 
- bucket-name: 存储文件夹名称
- system-platform: minio 服务器平台 unix 或 windows

### 使用
```java
import indi.chieftain.minio.toolkit.MinioHelper;

@Autowrite
private MinioHelper minioHelper;
```