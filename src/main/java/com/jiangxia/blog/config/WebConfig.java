package com.jiangxia.blog.config;

import com.jiangxia.blog.common.interceptor.GlobalRequestLoggingInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web配置类
 * 注册全局请求日志拦截器
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final GlobalRequestLoggingInterceptor requestLoggingInterceptor;

    public WebConfig(GlobalRequestLoggingInterceptor requestLoggingInterceptor) {
        this.requestLoggingInterceptor = requestLoggingInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册全局请求日志拦截器
        registry.addInterceptor(requestLoggingInterceptor)
                .addPathPatterns("/**")  // 拦截所有路径
                .excludePathPatterns(    // 排除静态资源和监控端点
                    "/static/**",
                    "/webjars/**",
                    "/favicon.ico",
                    "/actuator/**",
                    "/v3/api-docs/**",
                    "/swagger-ui/**"
                );
    }
}