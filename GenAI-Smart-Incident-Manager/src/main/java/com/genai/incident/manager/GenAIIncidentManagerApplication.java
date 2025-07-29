package com.genai.incident.manager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the GenAI Smart Incident Manager application.
 * This class uses Spring Boot's @SpringBootApplication annotation, which
 * combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.
 * It sets up the Spring context and starts the embedded Tomcat server.
 */
@SpringBootApplication
public class GenAIIncidentManagerApplication {

    public static void main(String[] args) {
        // Run the Spring Boot application. This method sets up the application context
        // and starts all necessary components, including REST controllers.
        SpringApplication.run(GenAIIncidentManagerApplication.class, args);
        System.out.println("GenAI Smart Incident Manager application started successfully!");
        System.out.println("Access the API at: http://localhost:8080/api/logs");
    }
}