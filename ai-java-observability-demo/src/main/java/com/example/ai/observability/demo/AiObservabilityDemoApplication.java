package com.example.ai.observability.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main Spring Boot application class.
 * This class serves as the entry point for the AI Observability Demo.
 * Spring Boot's @SpringBootApplication annotation enables auto-configuration,
 * component scanning, and is essentially a convenience annotation that adds:
 * - @Configuration: Tags the class as a source of bean definitions for the application context.
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
 *   other beans, and various property settings.
 * - @ComponentScan: Tells Spring to look for other components, configurations, and services
 *   in the 'com.example.ai.observability.demo' package, allowing it to find our controllers.
 */
@SpringBootApplication
public class AiObservabilityDemoApplication {

    public static void main(String[] args) {
        // Run the Spring Boot application.
        // When an observability agent (like Dynatrace or New Relic) is attached via -javaagent,
        // it will automatically instrument the JVM and application code during startup
        // to collect metrics, traces, and logs.
        SpringApplication.run(AiObservabilityDemoApplication.class, args);
    }

}