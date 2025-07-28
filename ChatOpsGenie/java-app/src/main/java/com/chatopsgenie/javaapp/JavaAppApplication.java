package com.chatopsgenie.javaapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController // Marks this class as a Spring web controller, handling HTTP requests
public class JavaAppApplication {

	public static void main(String[] args) {
		// Entry point for the Spring Boot application
		SpringApplication.run(JavaAppApplication.class, args);
	}

	// Defines an HTTP GET endpoint at the root path "/"
	@GetMapping("/")
	public String home() {
		// Returns a simple greeting message
		return "Hello from ChatOpsGenie Java App!";
	}

	// Defines an HTTP GET endpoint at "/greet"
	// It accepts an optional 'name' query parameter
	@GetMapping("/greet")
	public String greet(@RequestParam(value = "name", defaultValue = "World") String name) {
		// Returns a personalized greeting
		return String.format("Hello, %s! This is ChatOpsGenie Java App.", name);
	}

}