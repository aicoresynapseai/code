package com.autonomic.java.service.monitor;

import com.autonomic.java.service.ai.AnomalyDetector;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * ServiceMonitor is responsible for simulating and collecting runtime metrics
 * and feeding them to the AnomalyDetector.
 * It uses Spring's @Scheduled annotation to perform periodic monitoring.
 */
@Component
@RequiredArgsConstructor // Lombok: Creates a constructor for final fields (AnomalyDetector)
@Slf4j // Lombok: Provides a logger
public class ServiceMonitor {

    private final AnomalyDetector anomalyDetector; // Injects the AnomalyDetector to send metric data

    // AtomicReference to hold the current simulated metrics safely across threads
    @Getter // Lombok: Generates a getter for currentMetrics
    private final AtomicReference<Map<String, Double>> currentMetrics = new AtomicReference<>(new HashMap<>());

    // Simulated metric values. These will be updated by simulation endpoints and healing actions.
    @Setter // Lombok: Generates setters for these fields
    private volatile double simulatedCpuUsage = 20.0; // Default normal CPU usage
    @Setter
    private volatile double simulatedMemoryUsage = 30.0; // Default normal Memory usage
    @Setter
    private volatile double simulatedLatency = 100.0; // Default normal Response Latency (ms)

    /**
     * Initializes the current metrics map with default values when the component is created.
     */
    public ServiceMonitor() {
        // Initialize the map with some default values.
        // The AtomicReference will hold this map.
        currentMetrics.get().put("cpuUsage", simulatedCpuUsage);
        currentMetrics.get().put("memoryUsage", simulatedMemoryUsage);
        currentMetrics.get().put("latencyMs", simulatedLatency);
    }

    /**
     * Scheduled method to periodically collect and process simulated metrics.
     * This method runs every 5 seconds (5000 milliseconds).
     */
    @Scheduled(fixedRate = 5000) // Runs every 5 seconds
    public void collectMetrics() {
        // Update the simulated metrics in the map
        Map<String, Double> metrics = new HashMap<>();
        metrics.put("cpuUsage", simulatedCpuUsage);
        metrics.put("memoryUsage", simulatedMemoryUsage);
        metrics.put("latencyMs", simulatedLatency);

        // Update the AtomicReference with the new metrics map
        currentMetrics.set(metrics);

        log.info("ServiceMonitor: Collected metrics - CPU: {}%, Memory: {}%, Latency: {}ms",
                simulatedCpuUsage, simulatedMemoryUsage, simulatedLatency);

        // Pass the collected metrics to the AnomalyDetector for analysis
        anomalyDetector.detectAnomalies(metrics);
    }

    /**
     * Resets a specific simulated metric to a normal range.
     * Called by healing actions to revert the service to a healthy state after remediation.
     * @param metricType The type of metric to reset (e.g., "cpuUsage", "memoryUsage").
     * @param newValue The value to set the metric to.
     */
    public void resetSimulatedMetric(String metricType, double newValue) {
        log.info("ServiceMonitor: Resetting simulated metric '{}' to {}", metricType, newValue);
        switch (metricType) {
            case "cpuUsage":
                this.simulatedCpuUsage = newValue;
                break;
            case "memoryUsage":
                this.simulatedMemoryUsage = newValue;
                break;
            case "latencyMs":
                this.simulatedLatency = newValue;
                break;
            default:
                log.warn("ServiceMonitor: Attempted to reset unknown metric type: {}", metricType);
        }
        // Immediately update currentMetrics map after resetting
        currentMetrics.get().put(metricType, newValue);
    }
}