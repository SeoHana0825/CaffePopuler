package com.example.caffepopularproject.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig {

    @Bean(name = "dataPlatformTaskExecutor")
    public Executor dataPlatformTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5); // 기본 유지 스레드 수
        executor.setMaxPoolSize(10); // 최대 생성 가능한 스레드 재한 수
        executor.setQueueCapacity(50); // 스레드가 꽉 찼을 때 대기하는 큐 크기
        executor.setThreadNamePrefix("Mock-Async");
        executor.initialize();
        return executor;
    }
}
