# PowerShell 部署脚本
Write-Host "🚀 开始部署 Spring Boot 博客应用" -ForegroundColor Green

# 检查是否安装了必要的工具
try {
    $dockerVersion = docker --version
    Write-Host "✅ Docker 已安装: $dockerVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker 未安装或未在PATH中" -ForegroundColor Red
    exit 1
}

try {
    $dockerComposeVersion = docker-compose --version
    Write-Host "✅ Docker Compose 已安装: $dockerComposeVersion" -ForegroundColor Green
} catch {
    Write-Host "❌ Docker Compose 未安装或未在PATH中" -ForegroundColor Red
    exit 1
}

# 检查Maven
try {
    $mvnVersion = mvn --version
    Write-Host "✅ Maven 已安装" -ForegroundColor Green
} catch {
    Write-Host "❌ Maven 未安装或未在PATH中" -ForegroundColor Red
    exit 1
}

# 默认环境为开发环境
$Environment = if ($args.Count -gt 0) { $args[0] } else { "dev" }

Write-Host "📦 构建应用 JAR 文件" -ForegroundColor Yellow
mvn clean package -DskipTests

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Maven 构建失败" -ForegroundColor Red
    exit 1
}

Write-Host "🐳 构建 Docker 镜像" -ForegroundColor Yellow
docker build -t spring-boot-blog:latest .

if ($LASTEXITCODE -ne 0) {
    Write-Host "❌ Docker 构建失败" -ForegroundColor Red
    exit 1
}

switch ($Environment) {
    "dev" {
        Write-Host "🛠️ 启动开发环境" -ForegroundColor Yellow
        docker-compose -f docker-compose.dev.yml up -d
    }
    "prod" {
        Write-Host "🏭 启动生产环境" -ForegroundColor Yellow
        # 检查必要环境变量
        if (-not $env:PROD_DB_PASSWORD -or -not $env:PROD_JWT_SECRET) {
            Write-Host "❌ 生产环境变量未设置，请设置 PROD_DB_PASSWORD 和 PROD_JWT_SECRET" -ForegroundColor Red
            exit 1
        }
        docker-compose -f docker-compose.prod.yml up -d
    }
    default {
        Write-Host "📝 启动默认环境 (dev)" -ForegroundColor Yellow
        docker-compose -f docker-compose.yml up -d
    }
}

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ 部署完成！" -ForegroundColor Green
    Write-Host "🌐 应用访问地址: http://localhost:5001" -ForegroundColor Cyan
    Write-Host "📊 监控端点: http://localhost:5001/actuator/prometheus" -ForegroundColor Cyan
    
    # 显示容器状态
    Write-Host "`n📋 运行中的容器:" -ForegroundColor Yellow
    docker ps --filter "name=spring-boot-blog"
} else {
    Write-Host "❌ 部署失败" -ForegroundColor Red
    exit 1
}