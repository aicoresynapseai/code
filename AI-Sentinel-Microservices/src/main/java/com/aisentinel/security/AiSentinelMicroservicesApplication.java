package com.aisentinel.security;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// This annotation marks the main class of a Spring Boot application.
// It combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.
@SpringBootApplication
public class AiSentinelMicroservicesApplication {

    public static void main(String[] args) {
        // Entry point for the Spring Boot application.
        SpringApplication.run(AiSentinelMicroservicesApplication.class, args);
    }

    // You could optionally configure web MVC components here if needed,
    // for example, adding global interceptors directly.
    // However, for this example, the interceptor is registered in SecurityConfig
    // or as a Bean in AiSecurityInterceptor, which registers it automatically.
}