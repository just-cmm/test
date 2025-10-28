package com.example.maven;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 交通数据推送API应用主类
 * 符合GA/T 1049.2-2013标准的交通数据接收服务
 */
@SpringBootApplication
public class TrafficDataApiApplication implements WebMvcConfigurer {
    
    public static void main(String[] args) {
        SpringApplication.run(TrafficDataApiApplication.class, args);
    }
    
    /**
     * 配置CORS跨域支持
     * 允许交通系统跨域访问API
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}