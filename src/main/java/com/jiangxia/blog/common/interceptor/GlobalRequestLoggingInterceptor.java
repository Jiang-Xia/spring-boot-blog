package com.jiangxia.blog.common.interceptor;

import com.alibaba.fastjson.JSON;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 全局请求日志记录拦截器
 * 记录所有HTTP请求的详细信息，包括请求参数、响应参数等
 */
@Component
public class GlobalRequestLoggingInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(GlobalRequestLoggingInterceptor.class);
    
    // 请求开始时间的属性名
    private static final String REQUEST_START_TIME = "requestStartTime";
    private static final String REQUEST_ID = "requestId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 生成请求ID
        String requestId = UUID.randomUUID().toString();
        request.setAttribute(REQUEST_ID, requestId);
        
        // 记录请求开始时间
        long startTime = System.currentTimeMillis();
        request.setAttribute(REQUEST_START_TIME, startTime);
        
        // 记录请求基本信息
        String method = request.getMethod();
        String url = request.getRequestURL().toString();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String clientIp = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        
        // 记录请求头信息
        Map<String, String> headers = getRequestHeaders(request);
        
        // 记录请求参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        String parameters = formatParameters(parameterMap);
        
        // 构建完整的请求日志
        StringBuilder logBuilder = new StringBuilder();
        logBuilder.append("REQUEST_ID: ").append(requestId).append(" - ");
        logBuilder.append("HTTP ").append(method).append(" ").append(url);
        if (queryString != null) {
            logBuilder.append("?").append(queryString);
        }
        logBuilder.append(", IP: ").append(clientIp);
        logBuilder.append(", User-Agent: ").append(userAgent);
        logBuilder.append(", Headers: ").append(formatHeaders(headers));
        logBuilder.append(", Parameters: ").append(parameters);
        
        logger.info(logBuilder.toString());
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 获取请求开始时间
        Long startTime = (Long) request.getAttribute(REQUEST_START_TIME);
        String requestId = (String) request.getAttribute(REQUEST_ID);
        
        if (startTime != null) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            String method = request.getMethod();
            String uri = request.getRequestURI();
            int status = response.getStatus();
            
            // 记录响应信息
            logger.info("REQUEST_ID: {} - RESPONSE {} {}, Status: {}, Execution Time: {} ms", 
                       requestId, method, uri, status, executionTime);
            
            // 如果有异常，记录异常信息
            if (ex != null) {
                logger.error("REQUEST_ID: {} - EXCEPTION in {} {}: {}", 
                           requestId, method, uri, ex.getMessage(), ex);
            }
        }
    }
    
    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 如果是多个IP，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip != null ? ip : "Unknown";
    }
    
    /**
     * 获取请求头信息
     */
    private Map<String, String> getRequestHeaders(HttpServletRequest request) {
        Map<String, String> headers = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            String headerValue = request.getHeader(headerName);
            
            // 对敏感头信息进行脱敏处理
            if (isSensitiveHeader(headerName)) {
                headers.put(headerName, "SENSITIVE_HEADER_MASKED");
            } else {
                headers.put(headerName, headerValue);
            }
        }
        
        return headers;
    }
    
    /**
     * 判断是否为敏感头信息
     */
    private boolean isSensitiveHeader(String headerName) {
        if (headerName == null) {
            return false;
        }
        
        String lowerHeader = headerName.toLowerCase();
        return lowerHeader.contains("authorization") ||
               lowerHeader.contains("cookie") ||
               lowerHeader.contains("token") ||
               lowerHeader.contains("key");
    }
    
    /**
     * 格式化请求参数
     */
    private String formatParameters(Map<String, String[]> parameterMap) {
        if (parameterMap.isEmpty()) {
            return "{}";
        }
        
        Map<String, Object> formattedParams = new HashMap<>();
        
        for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
            String key = entry.getKey();
            String[] values = entry.getValue();
            
            // 对敏感参数进行脱敏处理
            if (isSensitiveParameter(key)) {
                formattedParams.put(key, "SENSITIVE_PARAM_MASKED");
            } else if (values.length == 1) {
                formattedParams.put(key, truncateString(values[0]));
            } else {
                String[] truncatedValues = new String[values.length];
                for (int i = 0; i < values.length; i++) {
                    truncatedValues[i] = truncateString(values[i]);
                }
                formattedParams.put(key, truncatedValues);
            }
        }
        
        try {
            return JSON.toJSONString(formattedParams);
        } catch (Exception e) {
            logger.warn("参数序列化失败", e);
            return formattedParams.toString();
        }
    }
    
    /**
     * 格式化请求头
     */
    private String formatHeaders(Map<String, String> headers) {
        try {
            return JSON.toJSONString(headers);
        } catch (Exception e) {
            logger.warn("请求头序列化失败", e);
            return headers.toString();
        }
    }
    
    /**
     * 判断是否为敏感参数
     */
    private boolean isSensitiveParameter(String paramName) {
        if (paramName == null) {
            return false;
        }
        
        String lowerParam = paramName.toLowerCase();
        return lowerParam.contains("password") ||
               lowerParam.contains("token") ||
               lowerParam.contains("secret") ||
               lowerParam.contains("key") ||
               lowerParam.contains("credential");
    }
    
    /**
     * 截断过长的字符串
     */
    private String truncateString(String str) {
        if (str == null) {
            return null;
        }
        
        final int MAX_LENGTH = 200;
        if (str.length() <= MAX_LENGTH) {
            return str;
        }
        return str.substring(0, MAX_LENGTH) + "...(truncated)";
    }
}