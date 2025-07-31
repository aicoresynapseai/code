package com.example.ai.observability.demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for demonstrating various application behaviors
 * that generate telemetry data (metrics, traces, logs) for observability tools.
 * Each endpoint is designed to simulate different types of load or issues.
 */
@RestController
@RequestMapping("/api")
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    /**
     * Handles a simple GET request.
     * This endpoint generates basic request metrics and traces.
     *
     * @return A greeting message.
     */
    @GetMapping("/hello")
    public String hello() {
        logger.info("Received request for /api/hello");
        return "Hello from AI Observability Demo App!";
    }

    /**
     * Simulates a CPU-intensive operation.
     * This will cause elevated CPU usage, which monitoring tools should detect.
     * It also introduces a slight delay.
     *
     * @param iterations The number of iterations for the CPU-bound loop. Defaults to 100,000.
     * @return A message indicating completion.
     */
    @GetMapping("/cpu-intensive")
    public String cpuIntensive(@RequestParam(defaultValue = "100000") int iterations) {
        logger.warn("Simulating CPU intensive task with {} iterations.", iterations);
        long startTime = System.currentTimeMillis();
        double result = 0;
        // Simple loop to consume CPU
        for (int i = 0; i < iterations; i++) {
            result += Math.sqrt(i) * Math.sin(i);
        }
        long endTime = System.currentTimeMillis();
        logger.warn("CPU intensive task completed in {} ms. Result: {}", (endTime - startTime), result);
        return String.format("CPU intensive task completed with %d iterations in %d ms.", iterations, (endTime - startTime));
    }

    /**
     * Simulates an application error.
     * This endpoint intentionally throws a RuntimeException,
     * which should be captured as an error by the monitoring agent,
     * appearing in logs and potentially as an anomaly/incident.
     *
     * @return An error response.
     */
    @GetMapping("/error")
    public ResponseEntity<String> generateError() {
        logger.error("Simulating an intentional error!");
        // An unhandled exception will result in a 500 Internal Server Error
        // and its stack trace will be logged and potentially traced by the agent.
        throw new RuntimeException("This is an intentional error for observability testing!");
    }

    /**
     * Simulates a network or external service call delay.
     * This will increase the response time for requests to this endpoint,
     * which should be visible in trace data and latency metrics.
     *
     * @param delayMs The delay in milliseconds. Defaults to 500ms.
     * @return A message indicating completion.
     */
    @GetMapping("/delay")
    public String delay(@RequestParam(defaultValue = "500") long delayMs) throws InterruptedException {
        logger.info("Simulating a delay of {} ms.", delayMs);
        Thread.sleep(delayMs); // Introduce a blocking delay
        logger.info("Delay completed.");
        return String.format("Operation completed after a simulated delay of %d ms.", delayMs);
    }
}