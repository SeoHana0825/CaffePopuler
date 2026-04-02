package com.example.caffepopularproject.common.config;

import com.example.caffepopularproject.common.interceptor.LoginInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors (InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .order(1) // 인터페이스 실행 순서
                .addPathPatterns("/api/orders/**", "/api/payments/**") // 검문
                .excludePathPatterns("/api/login","/api/signup"); // 검문 프리패스
    }
}
