package com.jiangxia.blog.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.util.Properties;

/**
 * 外部配置加载器，用于管理敏感配置
 */
@Component
public class ExternalConfigLoader {

    private final Environment environment;

    @Value("${app.external.config.path:${user.home}/.blog/config.properties}")
    private String externalConfigPath;

    public ExternalConfigLoader(Environment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadExternalConfig() {
        try {
            File configFile = new File(externalConfigPath);
            if (configFile.exists()) {
                Properties props = new Properties();
                props.load(java.nio.file.Files.newInputStream(configFile.toPath()));
                
                // 可以在这里处理额外的配置加载逻辑
                System.out.println("外部配置文件已加载: " + externalConfigPath);
            } else {
                System.out.println("外部配置文件不存在，使用默认配置: " + externalConfigPath);
            }
        } catch (IOException e) {
            System.err.println("加载外部配置文件失败: " + e.getMessage());
        }
    }

    /**
     * 获取数据库密码，优先从环境变量获取
     */
    public String getDatabasePassword() {
        String dbPassword = System.getenv("DB_PASSWORD");
        if (dbPassword == null || dbPassword.trim().isEmpty()) {
            dbPassword = environment.getProperty("spring.datasource.password");
        }
        return dbPassword;
    }

    /**
     * 获取JWT密钥，优先从环境变量获取
     */
    public String getJwtSecret() {
        String jwtSecret = System.getenv("JWT_SECRET");
        if (jwtSecret == null || jwtSecret.trim().isEmpty()) {
            jwtSecret = environment.getProperty("jwt.secret");
        }
        return jwtSecret;
    }

    /**
     * 获取Redis密码，优先从环境变量获取
     */
    public String getRedisPassword() {
        String redisPassword = System.getenv("REDIS_PASSWORD");
        if (redisPassword == null || redisPassword.trim().isEmpty()) {
            redisPassword = environment.getProperty("spring.redis.password");
        }
        return redisPassword;
    }

    /**
     * 获取邮箱密码，优先从环境变量获取
     */
    public String getMailPassword() {
        String mailPassword = System.getenv("MAIL_PASSWORD");
        if (mailPassword == null || mailPassword.trim().isEmpty()) {
            mailPassword = environment.getProperty("spring.mail.password");
        }
        return mailPassword;
    }
}