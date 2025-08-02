package com.devopsai.predictive.service;

import com.devopsai.predictive.model.BuildData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for predicting potential build/deployment issues.
 * This class simulates the interaction with a Generative AI model for analysis.
 * In a real scenario, this would involve sending prompts to an LLM API and parsing its responses.
 */
public class PredictionService {

    private static final Logger logger = LoggerFactory.getLogger(PredictionService.class);

    // Thresholds for anomaly detection (can be learned or configured)
    private static final int LONG_BUILD_THRESHOLD_SECONDS = 400; // Builds over 400 seconds might be a bottleneck
    private static final int RECENT_FAILURES_WINDOW_DAYS = 3; // Look for failures in the last 3 days

    /**
     * Analyzes historical build data to identify patterns and predict potential future issues.
     * This method contains the simulated GenAI logic.
     *
     * @param historicalData A list of historical BuildData objects.
     * @return A predictive analysis summary.
     */
    public String analyzeAndPredict(List<BuildData> historicalData) {
        if (historicalData == null || historicalData.isEmpty()) {
            return "No historical data available for prediction.";
        }

        StringBuilder predictionReport = new StringBuilder("--- Predictive Analytics Report ---\n");

        // Sort data by timestamp (most recent first) for time-based analysis
        List<BuildData> sortedData = historicalData.stream()
                .sorted(Comparator.comparing(BuildData::getTimestamp).reversed())
                .collect(Collectors.toList());

        // --- Simulated GenAI Analysis ---
        // This section mimics what a GenAI model might do by analyzing patterns in the data.
        // In a real application, you would construct a prompt with relevant data (error messages, trends, etc.)
        // and send it to an LLM API. The LLM would then generate insights.

        predictionReport.append("\n[Simulated GenAI Insights]\n");

        // 1. Analyze recent failures and their error messages
        OffsetDateTime threeDaysAgo = OffsetDateTime.now().minusDays(RECENT_FAILURES_WINDOW_DAYS);
        List<BuildData> recentFailures = sortedData.stream()
                .filter(b -> b.isFailure() && b.getTimestamp().isAfter(threeDaysAgo))
                .collect(Collectors.toList());

        if (!recentFailures.isEmpty()) {
            predictionReport.append("Recent build failures (last ").append(RECENT_FAILURES_WINDOW_DAYS).append(" days):\n");
            Map<String, Long> errorTypeCounts = recentFailures.stream()
                    .map(this::classifyErrorMessageGenAI) // Simulate GenAI classifying errors
                    .collect(Collectors.groupingBy(s -> s, Collectors.counting()));

            errorTypeCounts.forEach((type, count) ->
                    predictionReport.append("  - ").append(count).append("x ").append(type).append(" issues.\n"));

            // Simulate GenAI suggesting a potential common root cause
            if (errorTypeCounts.containsKey("Database Connection Issue") && errorTypeCounts.get("Database Connection Issue") > 1) {
                predictionReport.append("  -> GenAI suggests: Repeated 'Database Connection Issue' might indicate flaky DB server, network instability, or connection pool misconfiguration. Investigate DB server health and network connectivity.\n");
            } else if (errorTypeCounts.containsKey("Test Failure") && errorTypeCounts.get("Test Failure") > 2) {
                predictionReport.append("  -> GenAI suggests: Frequent 'Test Failure' could point to environmental differences between dev/CI, or brittle tests. Review recent code changes related to failing tests.\n");
            } else if (errorTypeCounts.containsKey("Deployment Environment Issue") && errorTypeCounts.get("Deployment Environment Issue") > 1) {
                predictionReport.append("  -> GenAI suggests: Multiple 'Deployment Environment Issue' errors hint at inconsistencies in target environments or infrastructure misconfiguration. Verify Kubernetes/VM settings.\n");
            } else if (errorTypeCounts.containsKey("Resource Exhaustion") && errorTypeCounts.get("Resource Exhaustion") > 0) {
                 predictionReport.append("  -> GenAI suggests: 'Resource Exhaustion' (OOM, Disk Space, CPU) indicates CI agent or target server might be undersized. Monitor resource usage closely.\n");
            } else {
                predictionReport.append("  -> GenAI notes: Diverse recent failures. Focus on individual error messages for specific remediation.\n");
            }

        } else {
            predictionReport.append("No build failures detected in the last ").append(RECENT_FAILURES_WINDOW_DAYS).append(" days. Good job!\n");
        }

        // 2. Identify long-running builds (potential bottlenecks)
        List<BuildData> longRunningBuilds = historicalData.stream()
                .filter(b -> b.getDurationSeconds() > LONG_BUILD_THRESHOLD_SECONDS)
                .sorted(Comparator.comparing(BuildData::getDurationSeconds).reversed())
                .collect(Collectors.toList());

        if (!longRunningBuilds.isEmpty()) {
            predictionReport.append("\nPotential Bottlenecks (Builds > ").append(LONG_BUILD_THRESHOLD_SECONDS).append("s):\n");
            longRunningBuilds.forEach(b ->
                    predictionReport.append(String.format("  - Build ID %s (Duration: %d s, Status: %s). Message: '%s'\n",
                            b.getBuildId(), b.getDurationSeconds(), b.getStatus(), b.getErrorMessage().isEmpty() ? "N/A" : b.getErrorMessage())));
            predictionReport.append("  -> GenAI suggests: Investigate these builds for specific long-running steps (e.g., slow tests, large dependency downloads, complex compilation). Optimize build agent performance or parallelize tasks.\n");
        } else {
            predictionReport.append("\nNo unusually long-running builds detected (all below ").append(LONG_BUILD_THRESHOLD_SECONDS).append("s).\n");
        }

        // 3. Look for trends in deployment failures
        List<BuildData> deploymentFailures = historicalData.stream()
                .filter(b -> !b.isDeploymentSuccess())
                .collect(Collectors.toList());

        if (!deploymentFailures.isEmpty()) {
            long totalDeployments = historicalData.stream().filter(b -> b.isSuccess() || b.isFailure()).count(); // Total builds that attempted deployment
            if (totalDeployments > 0) {
                double deploymentFailureRate = (double) deploymentFailures.size() / totalDeployments * 100;
                predictionReport.append(String.format("\nDeployment Failure Rate: %.2f%% (%d out of %d attempted deployments failed).\n",
                        deploymentFailureRate, deploymentFailures.size(), totalDeployments));
                if (deploymentFailureRate > 10) {
                    predictionReport.append("  -> GenAI warns: High deployment failure rate! This is a critical area. Focus on environment consistency, network stability, and correct application configurations.\n");
                }
            }
        } else {
            predictionReport.append("\nAll deployments recorded were successful. Excellent!\n");
        }


        predictionReport.append("\n--- End of Report ---\n");
        logger.info(predictionReport.toString());
        return predictionReport.toString();
    }

    /**
     * Simulates a GenAI model classifying an error message.
     * In a real system, this would be a prompt to an LLM like:
     * "Analyze the following build error message and categorize its root cause (e.g., 'Test Failure', 'Dependency Issue', 'Environment Misconfiguration', 'Resource Exhaustion', 'Network Issue'). Provide only the category. Message: [errorMessage]"
     * The LLM would then return a structured response which you would parse.
     *
     * @param buildData The BuildData object containing the error message.
     * @return A classified error type.
     */
    private String classifyErrorMessageGenAI(BuildData buildData) {
        String error = buildData.getErrorMessage().toLowerCase();
        if (error.contains("test fail") || error.contains("junit") || error.contains("assertion")) {
            return "Test Failure";
        } else if (error.contains("connect to database") || error.contains("sql exception") || error.contains("jdbc")) {
            return "Database Connection Issue";
        } else if (error.contains("out of memory") || error.contains("disk space") || error.contains("cpu usage")) {
            return "Resource Exhaustion";
        } else if (error.contains("dependency") || error.contains("maven") || error.contains("gradle") || error.contains("jar not found")) {
            return "Dependency Issue";
        } else if (error.contains("kubernetes") || error.contains("k8s") || error.contains("pod stuck") || error.contains("deployment timeout")) {
            return "Kubernetes/Orchestration Issue";
        } else if (error.contains("authentication expired") || error.contains("permission denied") || error.contains("access denied")) {
            return "Authentication/Authorization Issue";
        } else if (error.contains("network") || error.contains("unresponsive") || error.contains("firewall")) {
            return "Network Issue";
        } else if (error.contains("jvm startup") || error.contains("environment variable") || error.contains("config error")) {
            return "Deployment Environment Issue";
        } else if (error.contains("compilation error") || error.contains("syntax error")) {
            return "Code Compilation Issue";
        } else {
            return "Unknown/Generic Failure";
        }
    }
}