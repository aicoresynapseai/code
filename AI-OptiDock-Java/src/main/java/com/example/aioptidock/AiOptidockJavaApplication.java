package com.example.aioptidock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication // Marks this as a Spring Boot application
@RestController        // Marks this class as a REST controller
@RequestMapping("/api") // Base path for all endpoints in this controller
public class AiOptidockJavaApplication {

    public static void main(String[] args) {
        // Main method to run the Spring Boot application
        SpringApplication.run(AiOptidockJavaApplication.class, args);
    }

    /**
     * Defines a simple GET endpoint at /api/hello.
     * This will be our test endpoint for the Dockerized application.
     * @return A greeting message.
     */
    @GetMapping("/hello")
    public String hello() {
        return "Hello from AI-OptiDock-Java!"; // Returns a simple string response
    }
}