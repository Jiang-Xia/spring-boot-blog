#!/bin/bash

# 测试登录功能
BASE_URL="http://localhost:5001"

echo "测试验证码接口..."
response=$(curl -s "$BASE_URL/captcha")
echo "验证码响应: $response"

# 提取验证码ID
captcha_id=$(echo $response | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "验证码ID: $captcha_id"

echo "测试手机号登录..."
login_response=$(curl -s -X POST "$BASE_URL/user/login" \
  -H "Content-Type: application/json" \
  -d '{"mobile":"13800138000","password":"123456","authCode":"123456"}' \
  --cookie "captcha_id=$captcha_id")

echo "登录响应: $login_response"