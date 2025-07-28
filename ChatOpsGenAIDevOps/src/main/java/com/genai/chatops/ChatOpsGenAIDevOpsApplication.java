package com.genai.chatops;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(proxyTargetClass = true) // Enable AOP for event listeners
public class ChatOpsGenAIDevOpsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatOpsGenAIDevOpsApplication.class, args);
    }

}