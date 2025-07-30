package com.autonomic.java.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling; // Enable Spring's scheduled task execution

/**
 * Main Spring Boot application class for the AutonomicJavaService.
 * This class initializes the Spring context and starts the embedded web server.
 *
 * @EnableScheduling is crucial here as it allows Spring to find and execute methods
 * annotated with @Scheduled, which is used in ServiceMonitor for periodic metric collection.
 */
@SpringBootApplication
@EnableScheduling
public class AutonomicJavaServiceApplication {

	public static void main(String[] args) {
		// Bootstrap the Spring Boot application
		SpringApplication.run(AutonomicJavaServiceApplication.class, args);
		System.out.println("Autonomic Java Service is running. Access http://localhost:8080 for details.");
		System.out.println("Try /health, /metrics, or /simulate/cpu-spike, /simulate/memory-leak, /simulate/slow-response");
	}

}