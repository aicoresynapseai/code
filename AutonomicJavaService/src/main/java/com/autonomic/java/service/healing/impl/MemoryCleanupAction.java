package com.autonomic.java.service.healing.impl;

import com.autonomic.java.service.controller.ServiceController;
import com.autonomic.java.service.healing.AnomalyType;
import com.autonomic.java.service.healing.RemediationAction;
import com.autonomic.java.service.monitor.ServiceMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Remediation action for high memory usage, simulating a memory leak cleanup.
 * This action simulates clearing problematic caches or triggering garbage collection.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MemoryCleanupAction implements RemediationAction {

    private final ServiceMonitor serviceMonitor;
    private final ServiceController serviceController;

    @Override
    public void remediate() {
        log.info("MEMORY_CLEANUP_ACTION: Executing remediation - Triggering garbage collection / Clearing problematic caches...");
        // In a real scenario, this could involve:
        // - Evicting items from a cache
        // - Forcing a garbage collection (usually not recommended in production for explicit calls)
        // - Reloading a specific problematic component or bean
        // - Restarting a specific microservice instance (if part of a larger cluster)

        // For this simulation, we reset the simulated memory usage to a normal level
        serviceMonitor.resetSimulatedMetric("memoryUsage", 35.0); // Reset to a normal percentage
        serviceController.resetSimulatedFailure("HIGH_MEMORY");
        log.info("MEMORY_CLEANUP_ACTION: Simulated memory cleanup complete. Memory usage should normalize.");
    }

    @Override
    public String getActionName() {
        return "Memory Cleanup Action";
    }

    @Override
    public AnomalyType getAnomalyType() {
        return AnomalyType.HIGH_MEMORY;
    }
}