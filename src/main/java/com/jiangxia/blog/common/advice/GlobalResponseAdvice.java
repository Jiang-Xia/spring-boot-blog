package com.jiangxia.blog.common.advice;

import com.alibaba.fastjson.JSON;
import com.jiangxia.blog.common.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.Arrays;
import java.util.List;

/**
 * 全局响应统一包装拦截器
 * 自动将Controller返回的数据包装成统一的ApiResponse格式
 */
@RestControllerAdvice
class GlobalResponseAdvice implements ResponseBodyAdvice<Object> {

    private static final Logger logger = LoggerFactory.getLogger(GlobalResponseAdvice.class);
    
    // 白名单路径，不进行统一包装
    private static final List<String> WHITE_LIST_PATHS = Arrays.asList(
        "/user/authCode",           // 验证码接口
        "/v3/api-docs",        // Swagger API文档
        "/swagger-ui",         // Swagger UI
        "/webjars",            // Swagger静态资源
        "/favicon.ico"         // 网站图标
    );

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 检查是否需要进行响应包装
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, 
                                MethodParameter returnType,
                                MediaType selectedContentType,
                                Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                ServerHttpRequest request, 
                                ServerHttpResponse response) {
        
        // 获取当前请求路径
        String requestPath = getRequestPath(request);
        
        // 检查白名单
        if (isWhiteListPath(requestPath)) {
            logger.debug("白名单路径，跳过统一响应包装: {}", requestPath);
            return body;
        }
        
        // 如果返回值已经是ApiResponse类型，直接返回
        if (body instanceof ApiResponse) {
            logger.debug("响应已为ApiResponse格式，无需包装");
            return body;
        }
        
        // 如果返回值为String类型，需要特殊处理（避免类型转换错误）
        if (body instanceof String) {
            try {
                ApiResponse<String> apiResponse = ApiResponse.success((String) body);
                // String类型需要手动序列化，避免HttpMessageConverter处理错误
                return JSON.toJSONString(apiResponse);
            } catch (Exception e) {
                logger.error("String类型响应包装失败", e);
                return body;
            }
        }
        
        // 对于其他类型，直接包装
        logger.debug("自动包装响应数据为ApiResponse格式");
        return ApiResponse.success(body);
    }
    
    /**
     * 获取请求路径
     */
    private String getRequestPath(ServerHttpRequest request) {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            return servletRequest.getRequestURI();
        }
        return request.getURI().getPath();
    }
    
    /**
     * 判断是否为白名单路径
     */
    private boolean isWhiteListPath(String path) {
        return WHITE_LIST_PATHS.stream().anyMatch(path::startsWith);
    }
}