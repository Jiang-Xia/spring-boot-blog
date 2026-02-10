package com.jiangxia.blog.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 应用配置类
 */
@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class AppConfig {
    
    /**
     * 环境标识: dev(开发), test(测试), prod(生产)
     */
    private String environment = "dev";
    
    /**
     * 是否为开发环境
     */
    public boolean isDev() {
        return "dev".equals(environment);
    }
    
    /**
     * 是否为测试环境
     */
    public boolean isTest() {
        return "test".equals(environment);
    }
    
    /**
     * 是否为生产环境
     */
    public boolean isProd() {
        return "prod".equals(environment);
    }
    
    /**
     * 是否为开发或测试环境
     */
    public boolean isDevOrTest() {
        return isDev() || isTest();
    }
}