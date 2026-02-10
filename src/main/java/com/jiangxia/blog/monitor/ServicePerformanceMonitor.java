package com.jiangxia.blog.monitor;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.BaseUnits;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 服务性能监控类
 */
@Service
public class ServicePerformanceMonitor {

    private final MeterRegistry meterRegistry;
    
    // 计数器：记录各种操作的次数
    private Counter userLoginCounter;
    private Counter articleViewCounter;
    private Counter apiCallCounter;
    
    // 计时器：记录方法执行时间
    private Timer userLoginTimer;
    private Timer articleFetchTimer;
    
    // 当前活跃用户数
    private volatile int activeUsers = 0;
    
    public ServicePerformanceMonitor(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void init() {
        // 初始化计数器
        userLoginCounter = Counter.builder("user.login.count")
                .description("用户登录次数")
                .register(meterRegistry);
                
        articleViewCounter = Counter.builder("article.view.count")
                .description("文章浏览次数")
                .register(meterRegistry);
                
        apiCallCounter = Counter.builder("api.call.count")
                .description("API调用次数")
                .register(meterRegistry);
        
        // 初始化计时器
        userLoginTimer = Timer.builder("user.login.duration")
                .description("用户登录耗时")
                .register(meterRegistry);
                
        articleFetchTimer = Timer.builder("article.fetch.duration")
                .description("文章获取耗时")
                .register(meterRegistry);
        
        // 注册Gauge指标
        Gauge.builder("active.users", this, obj -> obj.getActiveUsers())
                .description("活跃用户数")
                .register(meterRegistry);
                
        Gauge.builder("jvm.memory.used", this, 
                    obj -> (double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))
                .description("JVM内存使用量")
                .baseUnit(BaseUnits.BYTES)
                .register(meterRegistry);
    }

    /**
     * 记录用户登录事件
     */
    public void recordUserLogin() {
        userLoginCounter.increment();
        activeUsers++;
    }

    /**
     * 记录文章浏览事件
     */
    public void recordArticleView() {
        articleViewCounter.increment();
    }

    /**
     * 记录API调用事件
     */
    public void recordApiCall() {
        apiCallCounter.increment();
    }

    /**
     * 记录用户登录耗时
     */
    public void recordUserLoginDuration(long durationMs) {
        userLoginTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 记录文章获取耗时
     */
    public void recordArticleFetchDuration(long durationMs) {
        articleFetchTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    /**
     * 用户登出时减少活跃用户数
     */
    public void decrementActiveUser() {
        if (activeUsers > 0) {
            activeUsers--;
        }
    }

    public int getActiveUsers() {
        return activeUsers;
    }
}