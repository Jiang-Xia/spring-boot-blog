#!/bin/bash

# Spring Boot 博客用户模块接口测试脚本
BASE_URL="http://localhost:5001"

echo "=== Spring Boot 博客用户模块接口测试 ==="
echo "测试时间: $(date)"
echo "基础URL: $BASE_URL"
echo ""

# 1. 测试验证码接口
echo "1. 测试验证码接口"
echo "GET $BASE_URL/captcha"
response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" "$BASE_URL/captcha")
http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
body=$(echo "$response" | sed '/HTTP_STATUS:/d')
echo "状态码: $http_status"
echo "响应体: $body"
echo ""

# 2. 测试公开认证接口
echo "2. 测试公开认证接口"
echo "GET $BASE_URL/auth/test"
response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" "$BASE_URL/auth/test")
http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
body=$(echo "$response" | sed '/HTTP_STATUS:/d')
echo "状态码: $http_status"
echo "响应体: $body"
echo ""

# 3. 测试Swagger UI
echo "3. 测试Swagger UI"
echo "GET $BASE_URL/swagger-ui.html"
response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" "$BASE_URL/swagger-ui.html")
http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
echo "状态码: $http_status"
echo ""

# 4. 测试API文档
echo "4. 测试API文档"
echo "GET $BASE_URL/v3/api-docs"
response=$(curl -s -w "\nHTTP_STATUS:%{http_code}" "$BASE_URL/v3/api-docs")
http_status=$(echo "$response" | grep "HTTP_STATUS:" | cut -d: -f2)
echo "状态码: $http_status"
echo ""

echo "=== 测试完成 ==="