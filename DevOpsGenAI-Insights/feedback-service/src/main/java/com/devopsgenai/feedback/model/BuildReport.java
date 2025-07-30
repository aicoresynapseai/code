package com.devopsgenai.feedback.model;

import lombok.Data; // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods
import java.util.List;

/**
 * Represents a build report received from a CI/CD pipeline.
 * This POJO (Plain Old Java Object) maps directly to the JSON payload
 * sent by the `java-app/run_build.sh` script.
 */
@Data // Automatically generates boilerplate code (getters, setters, etc.)
public class BuildReport {
    private String projectId; // Identifier for the project (e.g., "java-web-app")
    private long buildId;     // Unique identifier for a specific build run
    private String status;    // Build status: "SUCCESS" or "FAILURE"
    private int totalTests;   // Total number of tests executed
    private int passedTests;  // Number of tests that passed
    private int failedTests;  // Number of tests that failed
    private List<String> errorMessages; // List of specific error messages from failed tests/build
    private double buildTimeSeconds; // Time taken for the build in seconds

    // Lombok's @Data annotation automatically provides public getters and setters
    // for all fields, a no-argument constructor, equals(), hashCode(), and toString() methods.
    // If Lombok is not used, these methods would need to be manually implemented.
}