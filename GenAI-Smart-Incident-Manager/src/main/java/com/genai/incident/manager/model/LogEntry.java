package com.genai.incident.manager.model;

import java.time.Instant; // Using Instant for precise, machine-readable timestamps

/**
 * Represents a single log entry from an application.
 * This POJO (Plain Old Java Object) will be used to deserialize incoming JSON
 * log data from the API request.
 */
public class LogEntry {
    private Instant timestamp; // When the log was generated
    private String serviceName; // The name of the service/application that generated the log
    private String level;       // The log level (e.g., INFO, WARN, ERROR, DEBUG)
    private String message;     // The actual log message content

    // Default constructor is required for JSON deserialization by Spring/Jackson
    public LogEntry() {
    }

    public LogEntry(Instant timestamp, String serviceName, String level, String message) {
        this.timestamp = timestamp;
        this.serviceName = serviceName;
        this.level = level;
        this.message = message;
    }

    // --- Getters and Setters ---
    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "LogEntry{" +
               "timestamp=" + timestamp +
               ", serviceName='" + serviceName + '\'' +
               ", level='" + level + '\'' +
               ", message='" + message + '\'' +
               '}';
    }
}