# 验证码环境配置说明

## 功能说明

本项目实现了根据不同环境使用不同验证码校验策略的功能：

- **开发环境(dev)**: 使用固定验证码 `123456`
- **测试环境(test)**: 使用固定验证码 `123456`  
- **生产环境(prod)**: 进行真实验证码校验

## 环境配置

### 1. 环境配置文件

项目包含以下环境配置文件：

- `application.yml` - 主配置文件
- `application-dev.yml` - 开发环境配置
- `application-test.yml` - 测试环境配置
- `application-prod.yml` - 生产环境配置

### 2. 环境切换方式

通过设置 `spring.profiles.active` 参数来切换环境：

```bash
# 开发环境
java -jar blog-spring-boot-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 测试环境
java -jar blog-spring-boot-0.0.1-SNAPSHOT.jar --spring.profiles.active=test

# 生产环境
java -jar blog-spring-boot-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 3. 配置类

`AppConfig` 类提供了便捷的环境判断方法：

```java
@Autowired
private AppConfig appConfig;

// 环境判断
if (appConfig.isDev()) {
    // 开发环境逻辑
}

if (appConfig.isTest()) {
    // 测试环境逻辑
}

if (appConfig.isProd()) {
    // 生产环境逻辑
}

if (appConfig.isDevOrTest()) {
    // 开发或测试环境逻辑
}
```

## 验证码服务修改

### 1. 图片验证码服务 (CaptchaService)

```java
// 开发/测试环境
if (appConfig.isDevOrTest()) {
    // 直接验证是否为123456
    return code.equals("123456");
}

// 生产环境
// 从Redis获取真实验证码进行校验
```

### 2. 邮箱验证码服务 (EmailService)

```java
// 开发/测试环境
if (appConfig.isDevOrTest()) {
    // 直接验证是否为123456
    return code.equals("123456");
}

// 生产环境
// 从Redis获取真实验证码进行校验
```

## 使用示例

### 前端调用示例

```javascript
// 开发/测试环境
const loginData = {
    username: "test@example.com",
    password: "password123",
    captchaId: "any-value",  // 开发环境可以随意填写
    authCode: "123456"       // 固定验证码
};

// 生产环境
const loginData = {
    username: "test@example.com",
    password: "password123",
    captchaId: "actual-captcha-id",  // 真实验证码ID
    authCode: "actual-code"          // 真实验证码
};
```

## 错误提示

不同环境下错误提示会有所不同：

- **开发/测试环境**: "验证码错误，开发/测试环境请使用: 123456"
- **生产环境**: "验证码错误" 或 "验证码已过期或不存在"

## 测试验证

可以运行测试用例验证功能：

```bash
mvn test -Dtest=CaptchaServiceTest
```

## 注意事项

1. **安全提醒**: 固定验证码仅适用于开发和测试环境，严禁在生产环境使用
2. **环境标识**: 确保部署时正确设置环境参数
3. **Redis配置**: 生产环境需要正确配置Redis连接信息
4. **日志级别**: 不同环境的日志级别已分别配置，便于问题排查