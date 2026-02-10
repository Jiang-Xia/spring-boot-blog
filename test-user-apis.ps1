# Spring Boot 博客用户模块接口测试脚本
$baseUrl = "http://localhost:5001"

Write-Host "=== Spring Boot 博客用户模块接口测试 ===" -ForegroundColor Green
Write-Host "测试时间: $(Get-Date)" -ForegroundColor Yellow
Write-Host "基础URL: $baseUrl" -ForegroundColor Yellow
Write-Host ""

# 测试结果统计
$testResults = @{
    total = 0
    passed = 0
    failed = 0
}

function Test-Api {
    param(
        [string]$Name,
        [string]$Url,
        [string]$Method = "GET",
        [object]$Body = $null,
        [string]$ContentType = "application/json"
    )
    
    $testResults.total++
    Write-Host "$($testResults.total). 测试 $Name" -ForegroundColor Cyan
    Write-Host "$Method $Url" -ForegroundColor Gray
    
    try {
        $params = @{
            Uri = $Url
            Method = $Method
            UseBasicParsing = $true
        }
        
        if ($Body -ne $null) {
            $params.Body = $Body
            $params.ContentType = $ContentType
        }
        
        $response = Invoke-WebRequest @params
        Write-Host "状态码: $($response.StatusCode)" -ForegroundColor Green
        if ($response.Content.Length -lt 200) {
            Write-Host "响应体: $($response.Content)" -ForegroundColor Gray
        } else {
            Write-Host "响应体长度: $($response.Content.Length) 字符" -ForegroundColor Gray
        }
        $testResults.passed++
        Write-Host "✓ 通过" -ForegroundColor Green
    }
    catch {
        Write-Host "状态码: $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
        Write-Host "错误信息: $($_.Exception.Message)" -ForegroundColor Red
        if ($_.ErrorDetails) {
            Write-Host "详细错误: $($_.ErrorDetails.Message)" -ForegroundColor Red
        }
        $testResults.failed++
        Write-Host "✗ 失败" -ForegroundColor Red
    }
    Write-Host ""
}

# 1. 测试验证码接口
Test-Api -Name "验证码生成接口" -Url "$baseUrl/captcha" -Method "GET"

# 2. 测试验证码验证接口
$captchaResponse = Invoke-RestMethod -Uri "$baseUrl/captcha" -Method GET
$captchaId = $captchaResponse.data.id
$verifyData = @{
    id = $captchaId
    answer = "123456"
} | ConvertTo-Json

Test-Api -Name "验证码验证接口" -Url "$baseUrl/captcha/verify" -Method "POST" -Body $verifyData

# 3. 测试公开认证接口
Test-Api -Name "公开认证测试接口" -Url "$baseUrl/auth/test" -Method "GET"

# 4. 测试Swagger UI
Test-Api -Name "Swagger UI" -Url "$baseUrl/swagger-ui.html" -Method "GET"

# 5. 测试API文档
Test-Api -Name "OpenAPI 文档" -Url "$baseUrl/v3/api-docs" -Method "GET"

# 6. 测试用户注册接口
$registerData = @{
    username = "testuser$(Get-Random -Minimum 1000 -Maximum 9999)"
    password = "123456"
    nickname = "测试用户$(Get-Random -Minimum 1000 -Maximum 9999)"
    captchaId = $captchaId
    authCode = "123456"
} | ConvertTo-Json

Test-Api -Name "用户注册接口" -Url "$baseUrl/user/register" -Method "POST" -Body $registerData

# 7. 测试用户登录接口
$loginData = @{
    username = "testuser123"
    password = "123456"
    captchaId = $captchaId
    authCode = "123456"
} | ConvertTo-Json

Test-Api -Name "用户登录接口" -Url "$baseUrl/user/login" -Method "POST" -Body $loginData

# 8. 测试认证登录接口
$authLoginData = @{
    username = "testuser123"
    password = "123456"
} | ConvertTo-Json

Test-Api -Name "认证登录接口" -Url "$baseUrl/auth/login" -Method "POST" -Body $authLoginData

# 9. 测试邮箱注册接口
$emailRegisterData = @{
    email = "test$(Get-Random -Minimum 1000 -Maximum 9999)@example.com"
    password = "123456"
    nickname = "邮箱用户$(Get-Random -Minimum 1000 -Maximum 9999)"
    emailCode = "123456"
} | ConvertTo-Json

Test-Api -Name "邮箱注册接口" -Url "$baseUrl/user/email/register" -Method "POST" -Body $emailRegisterData

# 10. 测试邮箱登录接口
$emailLoginData = @{
    email = "test@example.com"
    emailCode = "123456"
} | ConvertTo-Json

Test-Api -Name "邮箱登录接口" -Url "$baseUrl/user/email/login" -Method "POST" -Body $emailLoginData

# 11. 测试发送邮箱验证码接口
$sendEmailData = @{
    email = "test@example.com"
    type = "REGISTER"
} | ConvertTo-Json

Test-Api -Name "发送邮箱验证码接口" -Url "$baseUrl/user/email/sendCode" -Method "POST" -Body $sendEmailData

# 12. 测试用户信息接口（需要认证，预期会失败）
Test-Api -Name "获取用户信息接口（未认证）" -Url "$baseUrl/user/info" -Method "GET"

# 13. 测试刷新Token接口
Test-Api -Name "刷新Token接口" -Url "$baseUrl/user/refresh?token=fake_token" -Method "GET"

# 14. 测试刷新认证Token接口
Test-Api -Name "刷新认证Token接口" -Url "$baseUrl/auth/refresh?refreshToken=fake_token" -Method "POST"

# 15. 测试GitHub OAuth回调接口
Test-Api -Name "GitHub OAuth回调接口" -Url "$baseUrl/auth/github/callback?code=test_code" -Method "GET"

Write-Host "=== 测试结果统计 ===" -ForegroundColor Green
Write-Host "总测试数: $($testResults.total)" -ForegroundColor Yellow
Write-Host "通过: $($testResults.passed)" -ForegroundColor Green
Write-Host "失败: $($testResults.failed)" -ForegroundColor Red
Write-Host "成功率: $(($testResults.passed/$testResults.total*100).ToString('F1'))%" -ForegroundColor Cyan
Write-Host "=== 测试完成 ===" -ForegroundColor Green