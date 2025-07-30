package com.autonomic.java.service.healing.impl;

import com.autonomic.java.service.controller.ServiceController;
import com.autonomic.java.service.healing.AnomalyType;
import com.autonomic.java.service.healing.RemediationAction;
import com.autonomic.java.service.monitor.ServiceMonitor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * Remediation action for high CPU usage.
 * This action simulates optimizing CPU-intensive operations and resetting the CPU metric.
 */
@Component
@RequiredArgsConstructor // Lombok: Generates constructor for final fields (dependencies)
@Slf4j // Lombok: Provides a logger
public class CpuOptimizationAction implements RemediationAction {

    private final ServiceMonitor serviceMonitor; // Injects ServiceMonitor to reset simulated CPU usage
    private final ServiceController serviceController; // Injects ServiceController to reset simulation flag

    @Override
    public void remediate() {
        log.info("CPU_OPTIMIZATION_ACTION: Executing remediation - Analyzing and optimizing CPU-intensive operations...");
        // In a real scenario, this could involve:
        // - Adjusting thread pool sizes
        // - Killing rogue threads (carefully!)
        // - Offloading computation
        // - Triggering a specific service restart
        // - Calling an external autoscaling mechanism

        // For this simulation, we reset the simulated CPU usage to a normal level
        serviceMonitor.resetSimulatedMetric("cpuUsage", 25.0); // Reset to a normal percentage
        serviceController.resetSimulatedFailure("HIGH_CPU"); // Reset the simulation flag in the controller
        log.info("CPU_OPTIMIZATION_ACTION: Simulated CPU optimization complete. CPU usage should normalize.");
    }

    @Override
    public String getActionName() {
        return "CPU Optimization Action";
    }

    @Override
    public AnomalyType getAnomalyType() {
        return AnomalyType.HIGH_CPU;
    }
}