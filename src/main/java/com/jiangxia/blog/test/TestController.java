package com.jiangxia.blog.test;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 测试控制器
 * 用于验证全局响应包装和日志记录功能
 */
@Tag(name = "测试模块", description = "功能测试相关接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @Operation(summary = "测试简单字符串返回", description = "测试全局响应包装对String类型的支持")
    @GetMapping("/string")
    public String testString() {
        return "Hello World";
    }

    @Operation(summary = "测试对象返回", description = "测试全局响应包装对对象类型的支持")
    @GetMapping("/object")
    public Map<String, Object> testObject() {
        Map<String, Object> result = new HashMap<>();
        result.put("name", "测试用户");
        result.put("age", 25);
        result.put("email", "test@example.com");
        return result;
    }

    @Operation(summary = "测试带参数的接口", description = "测试请求参数记录功能")
    @PostMapping("/params")
    public Map<String, Object> testParams(@RequestParam String name, 
                                         @RequestParam int age,
                                         @RequestBody Map<String, Object> data) {
        Map<String, Object> result = new HashMap<>();
        result.put("receivedName", name);
        result.put("receivedAge", age);
        result.put("receivedData", data);
        result.put("message", "参数接收成功");
        return result;
    }

    @Operation(summary = "测试敏感数据接口", description = "测试敏感数据脱敏功能")
    @PostMapping("/sensitive")
    public Map<String, Object> testSensitive(@RequestBody Map<String, Object> sensitiveData) {
        // 这个接口会返回包含敏感信息的数据，用于测试脱敏功能
        Map<String, Object> result = new HashMap<>();
        result.put("userData", sensitiveData);
        result.put("token", "fake-jwt-token-12345");
        result.put("apiKey", "secret-api-key-67890");
        return result;
    }

    @Operation(summary = "测试异常接口", description = "测试异常情况下的日志记录")
    @GetMapping("/error")
    public String testError() {
        throw new RuntimeException("这是一个测试异常");
    }
}