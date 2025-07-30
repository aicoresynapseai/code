package com.autonomic.java.service.controller;

import com.autonomic.java.service.monitor.ServiceMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * REST Controller for the AutonomicJavaService.
 * Provides endpoints for health checks, simulating failures, and viewing current metrics.
 */
@RestController
@RequestMapping("/")
@RequiredArgsConstructor // Lombok annotation to generate a constructor with required arguments (final fields)
@Slf4j // Lombok annotation to add a logger field
public class ServiceController {

    private final ServiceMonitor serviceMonitor; // Injects the ServiceMonitor to interact with metrics and simulations

    // AtomicBooleans to control the state of simulated failures
    private static final AtomicBoolean CPU_SPIKE_ACTIVE = new AtomicBoolean(false);
    private static final AtomicBoolean MEMORY_LEAK_ACTIVE = new AtomicBoolean(false);
    private static final AtomicBoolean SLOW_RESPONSE_ACTIVE = new AtomicBoolean(false);

    /**
     * Basic health check endpoint.
     * @return A simple "UP" status.
     */
    @GetMapping("/health")
    public String healthCheck() {
        log.info("Health check requested.");
        return "Status: UP";
    }

    /**
     * Endpoint to view the current simulated service metrics.
     * @return A map containing current CPU, Memory, and Latency metrics.
     */
    @GetMapping("/metrics")
    public Map<String, Double> getMetrics() {
        log.info("Metrics requested.");
        return serviceMonitor.getCurrentMetrics();
    }

    /**
     * Endpoint to simulate a CPU spike.
     * When activated, ServiceMonitor will report high CPU usage until remediated.
     * @return Confirmation message.
     */
    @GetMapping("/simulate/cpu-spike")
    public String simulateCpuSpike() {
        if (CPU_SPIKE_ACTIVE.compareAndSet(false, true)) {
            serviceMonitor.setSimulatedCpuUsage(95.0); // Set CPU to a high value
            log.warn("Simulating CPU spike activated!");
            return "CPU spike simulation activated. Monitor /metrics and logs for healing.";
        }
        return "CPU spike simulation already active.";
    }

    /**
     * Endpoint to simulate a memory leak.
     * When activated, ServiceMonitor will report high memory usage until remediated.
     * @return Confirmation message.
     */
    @GetMapping("/simulate/memory-leak")
    public String simulateMemoryLeak() {
        if (MEMORY_LEAK_ACTIVE.compareAndSet(false, true)) {
            serviceMonitor.setSimulatedMemoryUsage(85.0); // Set Memory to a high value
            log.warn("Simulating Memory leak activated!");
            return "Memory leak simulation activated. Monitor /metrics and logs for healing.";
        }
        return "Memory leak simulation already active.";
    }

    /**
     * Endpoint to simulate slow response times.
     * When activated, ServiceMonitor will report high latency until remediated.
     * @return Confirmation message.
     */
    @GetMapping("/simulate/slow-response")
    public String simulateSlowResponse() {
        if (SLOW_RESPONSE_ACTIVE.compareAndSet(false, true)) {
            serviceMonitor.setSimulatedLatency(5000.0); // Set Latency to a high value (5 seconds)
            log.warn("Simulating slow response activated!");
            return "Slow response simulation activated. Monitor /metrics and logs for healing.";
        }
        return "Slow response simulation already active.";
    }

    /**
     * Resets all simulated failure states to normal.
     * This is primarily used by the healing service to "fix" issues.
     * @param failureType The type of failure to reset.
     */
    public void resetSimulatedFailure(String failureType) {
        log.info("Resetting simulated failure: {}", failureType);
        switch (failureType) {
            case "HIGH_CPU":
                CPU_SPIKE_ACTIVE.set(false);
                serviceMonitor.setSimulatedCpuUsage(ThreadLocalRandom.current().nextDouble(10.0, 30.0)); // Restore to normal
                break;
            case "HIGH_MEMORY":
                MEMORY_LEAK_ACTIVE.set(false);
                serviceMonitor.setSimulatedMemoryUsage(ThreadLocalRandom.current().nextDouble(20.0, 40.0)); // Restore to normal
                break;
            case "SLOW_RESPONSE":
                SLOW_RESPONSE_ACTIVE.set(false);
                serviceMonitor.setSimulatedLatency(ThreadLocalRandom.current().nextDouble(50.0, 200.0)); // Restore to normal
                break;
            default:
                log.warn("Unknown failure type to reset: {}", failureType);
        }
    }
}