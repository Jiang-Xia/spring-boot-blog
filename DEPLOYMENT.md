# Spring Boot 博客应用 - 部署指南

## 🚀 部署方式

本项目支持多种部署方式：

### 1. Docker 部署（推荐）

#### 构建和启动

```bash
# 开发环境部署
docker-compose -f docker-compose.dev.yml up -d

# 生产环境部署
docker-compose -f docker-compose.prod.yml up -d

# 默认环境部署
docker-compose -f docker-compose.yml up -d
```

#### 使用部署脚本

```bash
# Windows PowerShell
.\scripts\deploy.ps1 [dev|prod]

# Linux/Mac
./scripts/deploy.sh [dev|prod]
```

### 2. 传统部署

```bash
# 构建应用
mvn clean package -DskipTests

# 启动应用（开发环境）
java -jar target/blog-spring-boot-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev

# 启动应用（生产环境）
java -jar target/blog-spring-boot-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

## 🔐 安全配置

### JWT 密钥管理
- 使用 SHA-256 哈希算法生成安全密钥
- 支持从环境变量获取密钥：`JWT_SECRET`
- 开发环境默认密钥：`xia-007-default-secret`

### 敏感配置管理
- 数据库密码：`DB_PASSWORD`
- Redis 密钥：`REDIS_PASSWORD`
- 邮件密码：`MAIL_PASSWORD`
- GitHub 客户端密钥：`GITHUB_CLIENT_SECRET`

## 📊 监控和日志

### 监控端点
- 健康检查：`/actuator/health`
- 指标监控：`/actuator/metrics`
- Prometheus：`/actuator/prometheus`
- 应用信息：`/actuator/info`

### 性能监控指标
- `user.login.count` - 用户登录次数
- `article.view.count` - 文章浏览次数
- `api.call.count` - API 调用次数
- `user.login.duration` - 用户登录耗时
- `article.fetch.duration` - 文章获取耗时
- `active.users` - 活跃用户数
- `jvm.memory.used` - JVM 内存使用量

### 日志配置
- 控制台日志格式化
- 文件日志滚动策略
- 敏感信息脱敏处理

## 🛡️ 权限控制

### 基于注解的权限控制
```java
@RequiresPermissions(value = {"user:read"}, logical = Logical.AND)
public String getUserInfo() {
    // 需要 user:read 权限
}

@RequiresPermissions(value = {"admin:user", "admin:post"}, logical = Logical.OR)
public String adminAction() {
    // 需要 admin:user 或 admin:post 任一权限
}
```

### URL 级别权限控制
- `/user/admin/**` - 仅管理员可访问
- `/user/info`, `/user/edit` - 登录用户可访问
- `/article/create`, `/article/edit` - 登录用户可访问

## 🌍 多环境支持

### 环境变量配置
```bash
# 开发环境
SPRING_PROFILES_ACTIVE=dev
DB_PASSWORD=jiangxia123!@#
JWT_SECRET=xia-007-default-secret-for-dev

# 生产环境
SPRING_PROFILES_ACTIVE=prod
PROD_DB_PASSWORD=your_secure_db_password
PROD_JWT_SECRET=your_secure_jwt_secret
PROD_REDIS_PASSWORD=your_secure_redis_password
PROD_MAIL_PASSWORD=your_secure_mail_password
```

## 🐳 Docker 配置

### 环境变量文件
创建 `.env` 文件：
```env
SPRING_PROFILES_ACTIVE=dev
DB_PASSWORD=jiangxia123!@#
JWT_SECRET=xia-007-default-secret
REDIS_PASSWORD=
MAIL_PASSWORD=BCqhazrCuMmiZyvh
```

### 容器健康检查
应用包含健康检查机制，确保服务正常运行后才对外提供服务。

## 📝 部署注意事项

1. **生产环境安全**：
   - 确保所有敏感配置通过环境变量或密钥管理
   - 使用强密码和安全的 JWT 密钥
   - 配置防火墙和访问控制

2. **数据库初始化**：
   - 首次部署时会自动执行 `database_init_rbac.sql`
   - 确保数据库用户有足够的权限

3. **资源限制**：
   - 生产环境已配置合理的 CPU 和内存限制
   - 根据实际需求调整资源配置

4. **监控告警**：
   - 集成 Prometheus 指标收集
   - 建议配置 Grafana 监控面板
   - 设置关键指标告警

## 🔧 常见问题

### 构建失败
- 确保 Maven 版本 >= 3.6.0
- 检查 Java 版本是否为 JDK 21
- 清理本地仓库缓存：`mvn dependency:purge-local-repository`

### 启动失败
- 检查数据库连接信息
- 确认 Redis 服务可用
- 查看日志文件定位具体错误

### 容器无法启动
- 检查端口占用情况
- 确认 Docker 和 Docker Compose 版本
- 验证环境变量配置

## 🔄 更新和维护

### 应用升级
1. 停止当前容器
2. 拉取最新代码
3. 重新构建镜像
4. 启动新容器

### 数据库迁移
- 使用 Liquibase 或 Flyway 进行数据库版本管理
- 在 `src/main/resources/db/migration` 目录下放置迁移脚本

---

**注意**：生产环境部署前请务必检查所有安全配置，确保敏感信息得到妥善保护。