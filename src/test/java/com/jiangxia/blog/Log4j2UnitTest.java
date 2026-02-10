package com.jiangxia.blog;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

/**
 * Log4j2配置单元测试
 * 不依赖Spring上下文，直接测试Log4j2配置
 */
class Log4j2UnitTest {

    private static final Logger logger = LogManager.getLogger(Log4j2UnitTest.class);
    private static final Logger apiLogger = LogManager.getLogger("com.jiangxia.blog.aspect.ApiLoggingAspect");
    private static final Logger errorLogger = LogManager.getLogger("errorLogger");

    @Test
    void testLog4j2BasicLogging() {
        System.out.println("=== 测试基础日志输出 ===");
        logger.info("测试基础日志输出 - INFO级别");
        logger.debug("测试基础日志输出 - DEBUG级别");
        logger.warn("测试基础日志输出 - WARN级别");
        logger.error("测试基础日志输出 - ERROR级别");
    }

    @Test
    void testApiLogger() {
        System.out.println("=== 测试API日志输出 ===");
        apiLogger.info("测试API日志输出 - INFO级别");
        apiLogger.debug("测试API日志输出 - DEBUG级别");
        apiLogger.warn("测试API日志输出 - WARN级别");
    }

    @Test
    void testErrorLogger() {
        System.out.println("=== 测试错误日志输出 ===");
        errorLogger.error("测试错误日志输出 - ERROR级别");
        errorLogger.error("测试带异常的错误日志", new RuntimeException("测试异常信息"));
    }

    @Test
    void testLogFormat() {
        System.out.println("=== 测试日志格式 ===");
        logger.info("测试日志格式 - 包含请求参数: {}", "testParam");
        logger.info("测试日志格式 - 包含响应数据: {}", "{\"id\": 1, \"name\": \"test\"}");
        
        // 模拟API日志
        apiLogger.info("REQUEST_ID: test-123 - GET http://localhost:8080/api/test - START TestController.testMethod, IP: 127.0.0.1, User-Agent: test-client, Request Params: {param1=value1}");
        apiLogger.info("REQUEST_ID: test-123 - GET http://localhost:8080/api/test - END TestController.testMethod, Execution Time: 150 ms, Response Params: {\"result\": \"success\"}");
    }

    @Test
    void testMultipleLogLevels() {
        System.out.println("=== 测试多级别日志输出 ===");
        // 测试不同级别的日志输出
        logger.trace("TRACE级别日志 - 最详细");
        logger.debug("DEBUG级别日志 - 调试信息");
        logger.info("INFO级别日志 - 一般信息");
        logger.warn("WARN级别日志 - 警告信息");
        logger.error("ERROR级别日志 - 错误信息");
        logger.fatal("FATAL级别日志 - 致命错误");
    }
}