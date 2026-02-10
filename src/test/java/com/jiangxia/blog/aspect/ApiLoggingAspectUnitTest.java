package com.jiangxia.blog.aspect;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API日志切面单元测试
 * 验证日志截断功能在不同环境下的行为
 */
class ApiLoggingAspectUnitTest {

    @Test
    void testTruncateStringInDevEnvironment() throws Exception {
        // 模拟开发环境
        System.setProperty("spring.profiles.active", "dev");
        
        ApiLoggingAspect aspect = new ApiLoggingAspect();
        Method method = ApiLoggingAspect.class.getDeclaredMethod("truncateString", String.class);
        method.setAccessible(true);
        
        String longString = "a".repeat(500);
        String result = (String) method.invoke(aspect, longString);
        
        // 开发环境应该返回完整字符串
        assertEquals(longString, result);
        assertFalse(result.contains("truncated"));
    }

    @Test
    void testTruncateStringInProdEnvironment() throws Exception {
        // 模拟生产环境
        System.setProperty("spring.profiles.active", "prod");
        
        ApiLoggingAspect aspect = new ApiLoggingAspect();
        Method method = ApiLoggingAspect.class.getDeclaredMethod("truncateString", String.class);
        method.setAccessible(true);
        
        String longString = "a".repeat(1500);
        String result = (String) method.invoke(aspect, longString);
        
        // 生产环境应该截断到1000字符
        assertTrue(result.length() > 1000); // 包含截断信息
        assertTrue(result.contains("truncated"));
        assertTrue(result.contains("total length: 1500"));
    }

    @Test
    void testShortStringNotTruncated() throws Exception {
        System.setProperty("spring.profiles.active", "prod");
        
        ApiLoggingAspect aspect = new ApiLoggingAspect();
        Method method = ApiLoggingAspect.class.getDeclaredMethod("truncateString", String.class);
        method.setAccessible(true);
        
        String shortString = "short response";
        String result = (String) method.invoke(aspect, shortString);
        
        // 短字符串不应该被截断
        assertEquals(shortString, result);
        assertFalse(result.contains("truncated"));
    }

    @Test
    void testTestEnvironmentAlsoShowsFullLog() throws Exception {
        // 测试环境也应该显示完整日志
        System.setProperty("spring.profiles.active", "test");
        
        ApiLoggingAspect aspect = new ApiLoggingAspect();
        Method method = ApiLoggingAspect.class.getDeclaredMethod("truncateString", String.class);
        method.setAccessible(true);
        
        String longString = "a".repeat(500);
        String result = (String) method.invoke(aspect, longString);
        
        // 测试环境应该返回完整字符串
        assertEquals(longString, result);
        assertFalse(result.contains("truncated"));
    }
}