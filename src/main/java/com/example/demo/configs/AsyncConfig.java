package com.example.demo.configs;

import java.lang.reflect.Method;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync(proxyTargetClass = true)
@EnableCaching
public class AsyncConfig implements AsyncConfigurer, AsyncUncaughtExceptionHandler {

    @Value("${async.threadPoolCoreSize:5}") private int threadPoolCoreSize;
    @Value("${async.threadPoolMaxSize:10}") private int threadPoolMaxSize;
    @Value("${async.threadPoolQueueCapacity:10}") private int threadPoolQueueCapacity;



    @Override
    @Bean("asyncExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(threadPoolCoreSize);
        executor.setMaxPoolSize(threadPoolMaxSize);
        executor.setQueueCapacity(threadPoolQueueCapacity);
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Override
    public void handleUncaughtException(Throwable throwable, Method method, Object... objects) {
        throwable.printStackTrace();
    }


}
