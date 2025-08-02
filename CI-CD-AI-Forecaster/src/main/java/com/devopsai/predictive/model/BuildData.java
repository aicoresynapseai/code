package com.devopsai.predictive.model;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single historical record of a CI/CD pipeline build or deployment.
 * This data would typically be pulled from CI/CD systems like Jenkins, GitLab CI, etc.
 */
public class BuildData {
    private String buildId;
    private OffsetDateTime timestamp;
    private int durationSeconds;
    private String status; // e.g., "SUCCESS", "FAILURE"
    private int testCount;
    private int failedTests;
    private boolean deploymentSuccess;
    private String errorMessage; // Simplified error message or log excerpt

    // Constructor
    public BuildData(String buildId, String timestamp, int durationSeconds, String status,
                     int testCount, int failedTests, boolean deploymentSuccess, String errorMessage) {
        this.buildId = buildId;
        // Parse the timestamp string to OffsetDateTime
        this.timestamp = OffsetDateTime.parse(timestamp, DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        this.durationSeconds = durationSeconds;
        this.status = status;
        this.testCount = testCount;
        this.failedTests = failedTests;
        this.deploymentSuccess = deploymentSuccess;
        this.errorMessage = errorMessage != null ? errorMessage.trim() : ""; // Ensure no nulls and trim
    }

    // Getters
    public String getBuildId() {
        return buildId;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public String getStatus() {
        return status;
    }

    public int getTestCount() {
        return testCount;
    }

    public int getFailedTests() {
        return failedTests;
    }

    public boolean isDeploymentSuccess() {
        return deploymentSuccess;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    // Helper methods for quick checks
    public boolean isSuccess() {
        return "SUCCESS".equalsIgnoreCase(status);
    }

    public boolean isFailure() {
        return "FAILURE".equalsIgnoreCase(status);
    }

    @Override
    public String toString() {
        return "BuildData{" +
               "buildId='" + buildId + '\'' +
               ", timestamp=" + timestamp +
               ", durationSeconds=" + durationSeconds +
               ", status='" + status + '\'' +
               ", testCount=" + testCount +
               ", failedTests=" + failedTests +
               ", deploymentSuccess=" + deploymentSuccess +
               ", errorMessage='" + errorMessage + '\'' +
               '}';
    }
}