package com.devopsgenai.feedback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Spring Boot Feedback Service application.
 * This class uses the @SpringBootApplication annotation, which is a convenience annotation
 * that adds:
 * - @Configuration: Tags the class as a source of bean definitions for the application context.
 * - @EnableAutoConfiguration: Tells Spring Boot to start adding beans based on classpath settings,
 *   other beans, and various property settings.
 * - @ComponentScan: Tells Spring to look for other components, configurations, and services
 *   in the `com.devopsgenai.feedback` package, allowing it to find controllers, services, etc.
 */
@SpringBootApplication
public class FeedbackServiceApplication {

    public static void main(String[] args) {
        // Run the Spring Boot application.
        // This will set up the embedded Tomcat server,
        // scan for components, and start the application.
        SpringApplication.run(FeedbackServiceApplication.class, args);
    }

}