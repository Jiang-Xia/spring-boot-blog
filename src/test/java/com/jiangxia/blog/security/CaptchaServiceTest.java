package com.jiangxia.blog.security;

import com.jiangxia.blog.common.exception.BizException;
import com.jiangxia.blog.config.AppConfig;
import com.jiangxia.blog.security.captcha.CaptchaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CaptchaServiceTest {

    private CaptchaService captchaService;
    private AppConfig appConfig;

    @BeforeEach
    void setUp() {
        appConfig = new AppConfig();
        appConfig.setEnvironment("dev");
        // 使用null作为RedisTemplate，因为我们只测试环境判断逻辑
        captchaService = new CaptchaService(null, appConfig);
    }

    @Test
    void testVerifyDevEnvironment_Success() {
        appConfig.setEnvironment("dev");
        boolean result = captchaService.verify("test-id", "123456");
        assertTrue(result);
    }

    @Test
    void testVerifyDevEnvironment_Failure() {
        appConfig.setEnvironment("dev");
        BizException exception = assertThrows(BizException.class, () -> {
            captchaService.verify("test-id", "wrong-code");
        });
        assertTrue(exception.getMessage().contains("验证码错误"));
    }

    @Test
    void testVerifyTestEnvironment_Success() {
        appConfig.setEnvironment("test");
        boolean result = captchaService.verify("test-id", "123456");
        assertTrue(result);
    }
}