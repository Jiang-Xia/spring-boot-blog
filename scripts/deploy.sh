#!/bin/bash

# 部署脚本
set -e

echo "🚀 开始部署 Spring Boot 博客应用"

# 检查是否安装了必要的工具
if ! command -v docker &> /dev/null; then
    echo "❌ Docker 未安装或未在PATH中"
    exit 1
fi

if ! command -v docker-compose &> /dev/null; then
    echo "❌ Docker Compose 未安装或未在PATH中"
    exit 1
fi

# 默认环境为开发环境
ENVIRONMENT=${1:-dev}

echo "📦 构建应用 JAR 文件"
mvn clean package -DskipTests

echo "🐳 构建 Docker 镜像"
docker build -t spring-boot-blog:latest .

case $ENVIRONMENT in
    "dev")
        echo "🛠️ 启动开发环境"
        docker-compose -f docker-compose.dev.yml up -d
        ;;
    "prod")
        echo "🏭 启动生产环境"
        # 检查必要环境变量
        if [[ -z "${PROD_DB_PASSWORD}" || -z "${PROD_JWT_SECRET}" ]]; then
            echo "❌ 生产环境变量未设置，请设置 PROD_DB_PASSWORD 和 PROD_JWT_SECRET"
            exit 1
        fi
        docker-compose -f docker-compose.prod.yml up -d
        ;;
    *)
        echo "📝 启动默认环境 (dev)"
        docker-compose -f docker-compose.yml up -d
        ;;
esac

echo "✅ 部署完成！"
echo "🌐 应用访问地址: http://localhost:5001"
echo "📊 监控端点: http://localhost:5001/actuator/prometheus"

# 显示容器状态
docker ps --filter "name=spring-boot-blog"