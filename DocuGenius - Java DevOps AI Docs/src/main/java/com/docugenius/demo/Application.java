package com.docugenius.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the DocuGenius Demo Spring Boot application.
 * This application serves as a simple example project whose code and configuration
 * will be analyzed by the GenAI documentation generation script.
 */
@SpringBootApplication
public class Application {

    /**
     * The main method that starts the Spring Boot application.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}