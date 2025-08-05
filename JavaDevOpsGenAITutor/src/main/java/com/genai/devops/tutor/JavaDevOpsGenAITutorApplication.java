package com.genai.devops.tutor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main application class for the JavaDevOpsGenAITutor.
 * This class uses Spring Boot's @SpringBootApplication annotation, which
 * combines @Configuration, @EnableAutoConfiguration, and @ComponentScan.
 * It's the entry point for the Spring Boot application.
 */
@SpringBootApplication
public class JavaDevOpsGenAITutorApplication {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command line arguments passed to the application.
     */
    public static void main(String[] args) {
        // Runs the Spring Boot application. This method sets up the Spring context,
        // scans for components, and starts the embedded web server (Tomcat by default).
        SpringApplication.run(JavaDevOpsGenAITutorApplication.class, args);
    }

}