package com.autonomic.java.service.ai;

import com.autonomic.java.service.healing.HealingService;
import com.autonomic.java.service.healing.AnomalyType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The "AI" component of the self-healing system.
 * This class is responsible for analyzing incoming metrics and detecting anomalies
 * based on predefined rules. In a real-world scenario, this could be replaced by
 * a more sophisticated machine learning model trained on historical data.
 */
@Service
@RequiredArgsConstructor // Lombok: Generates constructor for final fields (HealingService)
@Slf4j // Lombok: Provides a logger
public class AnomalyDetector {

    private final HealingService healingService; // Injects HealingService to trigger remediation

    // Thresholds for anomaly detection
    private static final double CPU_THRESHOLD = 90.0;
    private static final double MEMORY_THRESHOLD = 80.0;
    private static final double LATENCY_THRESHOLD = 1000.0; // 1 second

    // Counters to track how many consecutive times an anomaly is detected
    // This prevents triggering healing on transient spikes.
    private final Map<AnomalyType, AtomicInteger> anomalyConsecutiveCounts = new ConcurrentHashMap<>();
    private static final int CONSECUTIVE_DETECTION_THRESHOLD = 2; // Detect an anomaly if it persists for 2 consecutive checks

    /**
     * Analyzes a map of current metrics and detects anomalies based on defined thresholds.
     * If an anomaly is detected and persists for a configured number of checks,
     * it triggers the HealingService.
     *
     * @param metrics A map containing current metric values (e.g., "cpuUsage", "memoryUsage", "latencyMs").
     */
    public void detectAnomalies(Map<String, Double> metrics) {
        double cpuUsage = metrics.getOrDefault("cpuUsage", 0.0);
        double memoryUsage = metrics.getOrDefault("memoryUsage", 0.0);
        double latencyMs = metrics.getOrDefault("latencyMs", 0.0);

        log.debug("AnomalyDetector: Analyzing metrics - CPU: {}, Memory: {}, Latency: {}", cpuUsage, memoryUsage, latencyMs);

        // Check for HIGH_CPU anomaly
        if (cpuUsage > CPU_THRESHOLD) {
            incrementAnomalyCount(AnomalyType.HIGH_CPU);
            log.warn("AnomalyDetector: Potential HIGH_CPU detected (current: {}%). Consecutive detections: {}",
                    cpuUsage, anomalyConsecutiveCounts.get(AnomalyType.HIGH_CPU).get());
            if (anomalyConsecutiveCounts.get(AnomalyType.HIGH_CPU).get() >= CONSECUTIVE_DETECTION_THRESHOLD) {
                log.error("AnomalyDetector: Confirmed HIGH_CPU anomaly. Triggering healing.");
                healingService.performHealing(AnomalyType.HIGH_CPU);
                resetAnomalyCount(AnomalyType.HIGH_CPU); // Reset count after triggering healing
            }
        } else {
            resetAnomalyCount(AnomalyType.HIGH_CPU); // Reset if metric is back to normal
        }

        // Check for HIGH_MEMORY anomaly
        if (memoryUsage > MEMORY_THRESHOLD) {
            incrementAnomalyCount(AnomalyType.HIGH_MEMORY);
            log.warn("AnomalyDetector: Potential HIGH_MEMORY detected (current: {}%). Consecutive detections: {}",
                    memoryUsage, anomalyConsecutiveCounts.get(AnomalyType.HIGH_MEMORY).get());
            if (anomalyConsecutiveCounts.get(AnomalyType.HIGH_MEMORY).get() >= CONSECUTIVE_DETECTION_THRESHOLD) {
                log.error("AnomalyDetector: Confirmed HIGH_MEMORY anomaly. Triggering healing.");
                healingService.performHealing(AnomalyType.HIGH_MEMORY);
                resetAnomalyCount(AnomalyType.HIGH_MEMORY);
            }
        } else {
            resetAnomalyCount(AnomalyType.HIGH_MEMORY);
        }

        // Check for SLOW_RESPONSE anomaly
        if (latencyMs > LATENCY_THRESHOLD) {
            incrementAnomalyCount(AnomalyType.SLOW_RESPONSE);
            log.warn("AnomalyDetector: Potential SLOW_RESPONSE detected (current: {}ms). Consecutive detections: {}",
                    latencyMs, anomalyConsecutiveCounts.get(AnomalyType.SLOW_RESPONSE).get());
            if (anomalyConsecutiveCounts.get(AnomalyType.SLOW_RESPONSE).get() >= CONSECUTIVE_DETECTION_THRESHOLD) {
                log.error("AnomalyDetector: Confirmed SLOW_RESPONSE anomaly. Triggering healing.");
                healingService.performHealing(AnomalyType.SLOW_RESPONSE);
                resetAnomalyCount(AnomalyType.SLOW_RESPONSE);
            }
        } else {
            resetAnomalyCount(AnomalyType.SLOW_RESPONSE);
        }
    }

    /**
     * Increments the consecutive detection count for a given anomaly type.
     * Initializes the count if it's the first detection.
     * @param type The type of anomaly.
     */
    private void incrementAnomalyCount(AnomalyType type) {
        anomalyConsecutiveCounts.computeIfAbsent(type, k -> new AtomicInteger(0)).incrementAndGet();
    }

    /**
     * Resets the consecutive detection count for a given anomaly type to zero.
     * @param type The type of anomaly.
     */
    private void resetAnomalyCount(AnomalyType type) {
        anomalyConsecutiveCounts.computeIfPresent(type, (k, v) -> {
            if (v.get() > 0) { // Only log if it was previously detected
                log.debug("AnomalyDetector: Resetting consecutive count for {}", type);
            }
            v.set(0);
            return v;
        });
    }
}