# Spring Boot Blog 全局响应和日志功能说明

## 功能概述

本项目实现了两个核心功能：

1. **全局接口响应数据统一拦截和封装**
2. **全局记录生成日志接口请求参数和接口响应参数**

## 功能详情

### 1. 全局响应统一包装 (`GlobalResponseAdvice`)

**功能说明：**
- 自动将Controller返回的数据包装成统一的ApiResponse格式
- 避免在每个接口中手动调用`ApiResponse.success()`
- 支持String类型特殊处理，避免类型转换错误
- 白名单机制，对Swagger、验证码等特殊接口跳过包装

**实现位置：**
- `com.jiangxia.blog.common.advice.GlobalResponseAdvice`

**使用示例：**
```java
// 之前需要手动包装
@GetMapping("/old-way")
public ApiResponse<String> oldWay() {
    return ApiResponse.success("data");
}

// 现在可以直接返回业务数据
@GetMapping("/new-way")
public String newWay() {
    return "data"; // 自动包装为 ApiResponse.success("data")
}
```

**白名单路径：**
- `/authCode` - 验证码接口
- `/v3/api-docs` - Swagger API文档
- `/swagger-ui` - Swagger UI
- `/webjars` - Swagger静态资源
- `/favicon.ico` - 网站图标

### 2. 全局请求日志记录

#### 2.1 拦截器方式 (`GlobalRequestLoggingInterceptor`)

**功能说明：**
- 记录每个HTTP请求的详细信息
- 包含请求ID、时间戳、HTTP方法、URL、客户端IP、User-Agent等
- 自动脱敏敏感头信息（Authorization、Cookie、Token等）
- 记录请求执行时间和响应状态码

**实现位置：**
- `com.jiangxia.blog.common.interceptor.GlobalRequestLoggingInterceptor`
- `com.jiangxia.blog.config.WebConfig` (注册拦截器)

**日志格式示例：**
```
REQUEST_ID: 94bf83c8-3450-4df4-91ab-87ad6b36976d - HTTP GET http://localhost:5001/auth/test, IP: 0:0:0:0:0:0:0:1, User-Agent: Mozilla/5.0, Headers: {...}, Parameters: {}
RESPONSE GET /auth/test, Status: 200, Execution Time: 55 ms
```

#### 2.2 AOP切面方式 (`ApiLoggingAspect`)

**功能说明：**
- 记录Controller方法级别的详细日志
- 包含类名、方法名、执行时间、请求参数、响应参数
- 自动脱敏敏感数据（password、token、secret等）
- 支持异常情况的日志记录

**实现位置：**
- `com.jiangxia.blog.aspect.ApiLoggingAspect`

**日志格式示例：**
```
REQUEST_ID: 853af8d5-38da-4955-825b-9cfc47f532eb - GET http://localhost:5001/auth/test - START AuthController.publicTest, IP: 0:0:0:0:0:0:0:1, Request Params: {}
REQUEST_ID: 853af8d5-38da-4955-825b-9cfc47f532eb - GET http://localhost:5001/auth/test - END AuthController.publicTest, Execution Time: 1 ms, Response Params: "public auth test ok"
```

## 敏感数据脱敏规则

### 请求参数脱敏
- 包含`password`、`token`、`secret`、`key`、`credential`的参数会被脱敏

### 请求头脱敏
- `Authorization`、`Cookie`、`Token`、`Key`等敏感头信息会被脱敏

### 响应数据脱敏
- 包含敏感关键词的响应数据会被脱敏处理

## 测试验证

### 测试接口
项目提供了专门的测试控制器：
- `com.jiangxia.blog.test.TestController`

### 验证方法
1. 启动应用：`mvn spring-boot:run`
2. 访问测试接口：`GET /auth/test`
3. 查看控制台日志输出
4. 验证响应格式是否为统一的ApiResponse结构

### 预期结果
```json
{
  "code": 0,
  "message": "success", 
  "data": "public auth test ok"
}
```

## 配置说明

### 日志级别配置
在`application.yml`中可以调整日志级别：
```yaml
logging:
  level:
    com.jiangxia.blog.common.advice: DEBUG
    com.jiangxia.blog.common.interceptor: INFO
    com.jiangxia.blog.aspect: INFO
```

### 拦截器排除路径
在`WebConfig`中配置不需要拦截的路径：
```java
registry.addInterceptor(requestLoggingInterceptor)
        .excludePathPatterns(
            "/static/**",
            "/webjars/**", 
            "/favicon.ico",
            "/actuator/**"
        );
```

## 注意事项

1. **String类型特殊处理**：由于String类型的HttpMessageConverter处理机制，String返回值需要手动序列化
2. **白名单机制**：确保Swagger、验证码等特殊接口不被统一包装影响
3. **性能考虑**：日志记录会增加一定的性能开销，生产环境可调整日志级别
4. **敏感数据**：系统会自动脱敏处理敏感信息，但建议在业务层面也要注意数据安全
5. **日期时间序列化**：对于包含LocalDateTime等Java 8时间类型的复杂对象，在AOP日志切面中如果遇到序列化失败，会自动降级使用对象的简短表示形式，避免应用崩溃

## 依赖组件

- Spring Boot 3.2.0
- Spring AOP
- Jackson (JSON处理)
- SLF4J (日志框架)