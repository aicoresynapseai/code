package com.aisentinel.security.model;

import java.time.LocalDateTime;

/**
 * Represents a detected attack pattern or security anomaly.
 * This simple POJO holds information about a potential threat identified by the AI Guardian.
 */
public class AttackPattern {
    private String type;        // e.g., "SQL_INJECTION", "XSS", "ANOMALY"
    private String severity;    // e.g., "Critical", "High", "Medium", "Low"
    private String description; // A more detailed description of the detected threat
    private LocalDateTime detectionTime; // When the threat was detected

    // Default constructor for serialization (e.g., if used in a REST response)
    public AttackPattern() {
        this.detectionTime = LocalDateTime.now();
    }

    public AttackPattern(String type, String severity, String description) {
        this.type = type;
        this.severity = severity;
        this.description = description;
        this.detectionTime = LocalDateTime.now(); // Set detection time upon creation
    }

    // --- Getters and Setters ---

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDetectionTime() {
        return detectionTime;
    }

    public void setDetectionTime(LocalDateTime detectionTime) {
        this.detectionTime = detectionTime;
    }

    @Override
    public String toString() {
        return "AttackPattern{" +
               "type='" + type + '\'' +
               ", severity='" + severity + '\'' +
               ", description='" + description + '\'' +
               ", detectionTime=" + detectionTime +
               '}';
    }
}