# Log4j2 日志配置说明

## 概述
本项目已从默认的 Logback 日志框架切换到 Log4j2，并配置了完整的日志文件输出功能。

## 配置文件结构

### 主配置文件
- `log4j2-dev.xml` - 开发环境配置
- `log4j2-test.xml` - 测试环境配置  
- `log4j2-prod.xml` - 生产环境配置

### 环境配置激活
在 `application.yml` 中配置：
```yaml
logging:
  config: classpath:log4j2-${spring.profiles.active}.xml
```

## 日志文件说明

### 开发环境 (dev)
- **日志目录**: `logs/`
- **文件大小限制**: 100MB
- **保留天数**: 30天
- **日志文件**:
  - `application.log` - 应用程序主日志
  - `api.log` - API接口调用日志
  - `error.log` - 错误日志（仅记录ERROR级别）

### 测试环境 (test)
- **日志目录**: `logs/`
- **文件大小限制**: 50MB
- **保留天数**: 15天
- **日志文件**:
  - `test-application.log` - 应用程序主日志
  - `test-api.log` - API接口调用日志
  - `test-error.log` - 错误日志

### 生产环境 (prod)
- **日志目录**: `logs/`
- **文件大小限制**: 200MB
- **保留天数**: 60天
- **日志文件**:
  - `application.log` - 应用程序主日志
  - `api.log` - API接口调用日志
  - `error.log` - 错误日志
  - `access.log` - 访问日志

## 日志级别配置

### 开发环境
- `com.jiangxia.blog`: DEBUG
- `com.jiangxia.blog.aspect.ApiLoggingAspect`: DEBUG
- 根日志级别: INFO

### 生产环境
- `com.jiangxia.blog`: INFO
- `com.jiangxia.blog.aspect.ApiLoggingAspect`: INFO
- `org.hibernate`: WARN
- `org.springframework`: WARN
- 根日志级别: WARN

## 使用示例

### 在代码中使用日志
```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

private static final Logger logger = LogManager.getLogger(YourClass.class);
private static final Logger apiLogger = LogManager.getLogger("com.jiangxia.blog.aspect.ApiLoggingAspect");
private static final Logger errorLogger = LogManager.getLogger("errorLogger");

// 基础日志
logger.info("这是一条信息日志");
logger.debug("这是调试日志");
logger.warn("这是警告日志");
logger.error("这是错误日志");

// API日志
apiLogger.info("API调用日志信息");

// 错误日志
errorLogger.error("错误信息", exception);
```

## 日志格式
```
2026-02-10 18:02:50.234 [main] INFO  com.jiangxia.blog.Log4j2UnitTest - 日志消息内容
```

格式说明：
- 时间戳: `yyyy-MM-dd HH:mm:ss.SSS`
- 线程名: `[thread-name]`
- 日志级别: `LEVEL`
- Logger名称: `logger.name`
- 日志消息: `message`

## 日志轮转策略
- **时间轮转**: 每天生成新的日志文件
- **大小轮转**: 当文件达到指定大小时轮转
- **压缩**: 轮转后的文件自动压缩为 `.gz` 格式
- **保留策略**: 超过保留天数的文件自动删除

## 验证配置
运行测试验证配置是否正确：
```bash
mvn test -Dtest=Log4j2UnitTest
```

检查生成的日志文件：
```bash
ls logs/
```

## 注意事项
1. 确保 `logs` 目录有写入权限
2. 生产环境建议定期清理旧日志文件
3. 可根据实际需求调整文件大小和保留天数
4. 错误日志文件仅记录 ERROR 级别日志