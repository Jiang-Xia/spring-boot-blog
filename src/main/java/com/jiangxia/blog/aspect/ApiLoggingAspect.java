package com.jiangxia.blog.aspect;

import com.alibaba.fastjson.JSON;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * API日志记录切面
 * 记录接口请求参数和响应参数
 */
@Aspect
@Component
public class ApiLoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(ApiLoggingAspect.class);

    /**
     * 定义切点：所有控制器方法
     */
    @Pointcut("execution(* com.jiangxia.blog.*.controller..*(..))")
    public void controllerMethods() {}

    /**
     * 环绕通知：记录方法执行时间、请求参数和响应参数
     */
    @Around("controllerMethods()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = getCurrentRequest();
        String requestId = UUID.randomUUID().toString();
        
        long startTime = System.currentTimeMillis();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        String method = request != null ? request.getMethod() : "UNKNOWN";
        String url = request != null ? request.getRequestURL().toString() : "UNKNOWN";
        String userAgent = request != null ? request.getHeader("User-Agent") : "Unknown";
        String clientIp = getClientIpAddress(request);
        
        // 记录请求参数
        String requestParams = formatArguments(args);
        logger.info("REQUEST_ID: {} - {} {} - START {}.{}, IP: {}, User-Agent: {}, Request Params: {}", 
                   requestId, method, url, className, methodName, clientIp, userAgent, requestParams);

        try {
            Object result = joinPoint.proceed();
            
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            // 记录响应参数
            String responseParams = formatResponse(result);
            logger.info("REQUEST_ID: {} - {} {} - END {}.{}, Execution Time: {} ms, Response Params: {}", 
                       requestId, method, url, className, methodName, executionTime, responseParams);
            
            return result;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long executionTime = endTime - startTime;
            
            logger.error("REQUEST_ID: {} - {} {} - ERROR in {}.{}, Execution Time: {} ms, Exception: {}", 
                        requestId, method, url, className, methodName, executionTime, e.getMessage(), e);
            
            throw e;
        }
    }

    /**
     * 异常通知：记录异常信息
     */
    @AfterThrowing(pointcut = "controllerMethods()", throwing = "ex")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable ex) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        
        logger.error("Exception in {}.{}(): {}", className, methodName, ex.getMessage(), ex);
    }

    /**
     * 获取当前HTTP请求
     */
    private HttpServletRequest getCurrentRequest() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getRequest() : null;
    }

    /**
     * 获取客户端IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "Unknown";
        }
        
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
        
        return ip;
    }

    /**
     * 格式化请求参数，避免敏感信息泄露
     */
    private String formatArguments(Object[] args) {
        if (args == null || args.length == 0) {
            return "{}";
        }
        
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) {
                sb.append(", ");
            }
            
            Object arg = args[i];
            if (arg == null) {
                sb.append("null");
            } else {
                // 对参数进行序列化并脱敏
                String argStr = serializeAndMaskArgument(arg);
                sb.append(argStr);
            }
        }
        sb.append("}");
        
        return sb.toString();
    }
    
    /**
     * 格式化响应参数
     */
    private String formatResponse(Object response) {
        if (response == null) {
            return "null";
        }
        
        try {
            // 对响应数据进行序列化
            String responseStr = JSON.toJSONString(response);
            
            // 对敏感字段进行精准脱敏处理
            String maskedResponse = maskSensitiveFields(responseStr);
            
            return truncateString(maskedResponse);
        } catch (Exception e) {
            logger.debug("响应参数序列化失败，使用toString方法替代", e);
            // 如果序列化失败，返回简化版本
            return truncateString(response.getClass().getSimpleName() + "@" + Integer.toHexString(response.hashCode()));
        }
    }
    
    /**
     * 判断是否为敏感响应数据
     */
    private boolean isSensitiveResponse(String responseStr) {
        if (responseStr == null) {
            return false;
        }
        
        String lowerResponse = responseStr.toLowerCase();
        // 更精确的敏感数据检测，避免误判正常的token响应
        return (lowerResponse.contains("\"password\"") && lowerResponse.contains(":")) ||
               (lowerResponse.contains("\"secret\"") && lowerResponse.contains(":")) ||
               (lowerResponse.contains("\"key\"") && lowerResponse.contains(":")) ||
               (lowerResponse.contains("\"credential\"") && lowerResponse.contains(":"));
    }

    /**
     * 判断是否为敏感数据
     */
    private boolean isSensitiveData(Object obj) {
        if (obj == null) {
            return false;
        }
        
        // 特殊处理DTO对象，使用JSON序列化而不是toString()
        if (obj.getClass().getPackageName().contains("dto")) {
            try {
                String jsonStr = JSON.toJSONString(obj);
                String lowerJson = jsonStr.toLowerCase();
                return lowerJson.contains("\"password\"") ||
                       lowerJson.contains("\"secret\"") ||
                       lowerJson.contains("\"credential\"");
            } catch (Exception e) {
                // 序列化失败则检查toString()
                String objStr = obj.toString().toLowerCase();
                return objStr.contains("password") && 
                       (objStr.contains("field") || objStr.contains("password="));
            }
        }
        
        String objStr = obj.toString().toLowerCase();
        return (objStr.contains("password") && (objStr.contains("field") || objStr.contains("password="))) || 
               (objStr.contains("token") && objStr.contains("=")) || 
               objStr.contains("secret") ||
               (objStr.contains("key") && objStr.contains("=")) ||
               (objStr.contains("credential") && objStr.contains("="));
    }

    /**
     * 截断过长的字符串
     * 开发环境显示完整日志，生产环境适度截断
     */
    private String truncateString(String str) {
        // 开发环境下显示完整日志
        String activeProfile = System.getProperty("spring.profiles.active", "dev");
        if ("dev".equals(activeProfile) || "test".equals(activeProfile)) {
            return str;
        }
        
        // 生产环境适度截断（增加到1000字符）
        final int MAX_LENGTH = 1000;
        if (str.length() <= MAX_LENGTH) {
            return str;
        }
        return str.substring(0, MAX_LENGTH) + "...(truncated, total length: " + str.length() + ")";
    }
    
    /**
     * 对敏感字段进行脱敏处理
     */
    private String maskSensitiveFields(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            return jsonStr;
        }
        
        String result = jsonStr;
        
        // 脱敏密码字段
        result = result.replaceAll("\"password\"\\s*:\\s*\"[^\"]*\"", "\"password\":\"***\"");
        result = result.replaceAll("\"passwordRepeat\"\\s*:\\s*\"[^\"]*\"", "\"passwordRepeat\":\"***\"");
        
        // 脱敏密钥相关字段
        result = result.replaceAll("\"secret\"\\s*:\\s*\"[^\"]*\"", "\"secret\":\"***\"");
        result = result.replaceAll("\"key\"\\s*:\\s*\"[^\"]*\"", "\"key\":\"***\"");
        result = result.replaceAll("\"credential\"\\s*:\\s*\"[^\"]*\"", "\"credential\":\"***\"");
        
        // 脱敏认证相关字段（但保留token字段的基本信息）
        result = result.replaceAll("\"accessToken\"\\s*:\\s*\"[^\"]*\"", "\"accessToken\":\"***\"");
        result = result.replaceAll("\"refreshToken\"\\s*:\\s*\"[^\"]*\"", "\"refreshToken\":\"***\"");
        
        return result;
    }
    
    /**
     * 序列化参数并进行脱敏处理
     */
    private String serializeAndMaskArgument(Object arg) {
        try {
            // 特殊处理DTO对象
            if (arg.getClass().getPackageName().contains("dto")) {
                String jsonStr = JSON.toJSONString(arg);
                return maskSensitiveFields(jsonStr);
            }
            
            // 其他对象使用toString并截断
            return truncateString(arg.toString());
        } catch (Exception e) {
            logger.debug("参数序列化失败，使用toString方法替代", e);
            return truncateString(arg.toString());
        }
    }
}