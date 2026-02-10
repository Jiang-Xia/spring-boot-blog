package com.jiangxia.blog.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 敏感配置属性类
 * 用于管理敏感配置信息，支持从环境变量或配置文件读取
 */
@Component
@ConfigurationProperties(prefix = "app.sensitive")
public class SensitiveConfigProperties {

    // 数据库配置
    private String dbPassword;
    
    // JWT配置
    private String jwtSecret;
    
    // Redis配置
    private String redisPassword;
    
    // 邮件配置
    private String mailPassword;
    
    // GitHub OAuth配置
    private String githubClientId;
    private String githubClientSecret;

    public String getDbPassword() {
        return dbPassword != null ? dbPassword : System.getenv("DB_PASSWORD");
    }

    public void setDbPassword(String dbPassword) {
        this.dbPassword = dbPassword;
    }

    public String getJwtSecret() {
        return jwtSecret != null ? jwtSecret : System.getenv("JWT_SECRET");
    }

    public void setJwtSecret(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }

    public String getRedisPassword() {
        return redisPassword != null ? redisPassword : System.getenv("REDIS_PASSWORD");
    }

    public void setRedisPassword(String redisPassword) {
        this.redisPassword = redisPassword;
    }

    public String getMailPassword() {
        return mailPassword != null ? mailPassword : System.getenv("MAIL_PASSWORD");
    }

    public void setMailPassword(String mailPassword) {
        this.mailPassword = mailPassword;
    }

    public String getGithubClientId() {
        return githubClientId != null ? githubClientId : System.getenv("GITHUB_CLIENT_ID");
    }

    public void setGithubClientId(String githubClientId) {
        this.githubClientId = githubClientId;
    }

    public String getGithubClientSecret() {
        return githubClientSecret != null ? githubClientSecret : System.getenv("GITHUB_CLIENT_SECRET");
    }

    public void setGithubClientSecret(String githubClientSecret) {
        this.githubClientSecret = githubClientSecret;
    }
}