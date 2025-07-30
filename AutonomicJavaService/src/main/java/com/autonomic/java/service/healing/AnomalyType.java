package com.autonomic.java.service.healing;

/**
 * Enum to define different types of anomalies that the system can detect.
 * This provides a clear classification for the AnomalyDetector and helps
 * map to specific remediation actions.
 */
public enum AnomalyType {
    HIGH_CPU,       // Indicates CPU utilization is unusually high.
    HIGH_MEMORY,    // Indicates memory usage is unusually high, potentially a leak.
    SLOW_RESPONSE   // Indicates service response times are consistently slow.
}