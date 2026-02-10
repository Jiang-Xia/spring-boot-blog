package com.jiangxia.blog.security.email;

import com.jiangxia.blog.common.exception.BizException;
import com.jiangxia.blog.config.AppConfig;
import com.jiangxia.blog.user.entity.EmailCodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * 邮箱验证码服务
 * 注意：这是简化版本，实际生产环境需要配置真实的邮件服务
 */
@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private static final int CODE_LENGTH = 6;
    private static final long CODE_EXPIRE_TIME = TimeUnit.MINUTES.toSeconds(5); // 5分钟过期
    private static final long SEND_INTERVAL = TimeUnit.MINUTES.toSeconds(1); // 发送间隔1分钟
    private static final String EMAIL_CODE_PREFIX = "email_verification_code:";
    private static final String SEND_TIME_PREFIX = "email_send_time:";
    private static final String DEV_TEST_CODE = "123456"; // 开发测试环境固定验证码
    
    private final RedisTemplate<String, String> redisTemplate;
    private final Random random = new Random();
    private final AppConfig appConfig;
    
    public EmailService(RedisTemplate<String, String> redisTemplate, AppConfig appConfig) {
        this.redisTemplate = redisTemplate;
        this.appConfig = appConfig;
    }

    /**
     * 发送邮箱验证码
     */
    public void sendVerificationCode(String email, EmailCodeType type) {
        String code = generateCode();
        long now = System.currentTimeMillis();
        
        // 存储验证码到Redis
        String codeKey = EMAIL_CODE_PREFIX + type.getCode() + ":" + email;
        String timeKey = SEND_TIME_PREFIX + type.getCode() + ":" + email;
        
        redisTemplate.opsForValue().set(codeKey, code, CODE_EXPIRE_TIME, TimeUnit.SECONDS);
        redisTemplate.opsForValue().set(timeKey, String.valueOf(now), CODE_EXPIRE_TIME, TimeUnit.SECONDS);
        
        // 实际发送邮件（这里只是日志记录）
        log.info("发送邮箱验证码: email={}, type={}, code={}", email, type.getDescription(), code);
        
        // TODO: 实际项目中需要配置邮件服务器并发送邮件
        // 例如使用 Spring Mail 或第三方邮件服务
    }

    /**
     * 验证邮箱验证码
     */
    public void verifyCode(String email, String code, String type) {
        // 开发和测试环境使用固定验证码123456
        if (appConfig.isDevOrTest()) {
            if (!DEV_TEST_CODE.equals(code)) {
                throw new BizException("验证码错误，开发/测试环境请使用: " + DEV_TEST_CODE);
            }
            return;
        }
        
        // 生产环境进行真实校验
        String codeKey = EMAIL_CODE_PREFIX + type + ":" + email;
        String storedCode = redisTemplate.opsForValue().get(codeKey);
        
        if (storedCode == null) {
            throw new BizException("验证码已过期或不存在");
        }
        
        if (!storedCode.equals(code)) {
            throw new BizException("验证码错误");
        }
        
        // 验证成功后删除验证码
        String timeKey = SEND_TIME_PREFIX + type + ":" + email;
        redisTemplate.delete(codeKey);
        redisTemplate.delete(timeKey);
    }

    /**
     * 检查发送频率
     */
    public void checkSendFrequency(String email, EmailCodeType type) {
        String timeKey = SEND_TIME_PREFIX + type.getCode() + ":" + email;
        String lastSendTimeStr = redisTemplate.opsForValue().get(timeKey);
        
        if (lastSendTimeStr != null) {
            long lastSendTime = Long.parseLong(lastSendTimeStr);
            long timeSinceLastSend = System.currentTimeMillis() - lastSendTime;
            if (timeSinceLastSend < TimeUnit.SECONDS.toMillis(SEND_INTERVAL)) {
                long remainingSeconds = (TimeUnit.SECONDS.toMillis(SEND_INTERVAL) - timeSinceLastSend) / 1000;
                throw new BizException("发送验证码过于频繁，请" + remainingSeconds + "秒后再试");
            }
        }
    }

    /**
     * 生成验证码
     */
    private String generateCode() {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(random.nextInt(10));
        }
        return code.toString();
    }

    /**
     * 构建存储键
     */
    private String buildKey(String email, String type) {
        return EMAIL_CODE_PREFIX + type + ":" + email;
    }
}