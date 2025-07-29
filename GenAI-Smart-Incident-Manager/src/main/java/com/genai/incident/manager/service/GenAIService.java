package com.genai.incident.manager.service;

import com.genai.incident.manager.model.IncidentReport;
import com.genai.incident.manager.model.LogEntry;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID; // Used to generate unique IDs for incident reports

/**
 * This service simulates the interaction with a Generative AI model.
 * In a real-world scenario, this class would make API calls to an external LLM
 * (e.g., OpenAI, Google Gemini, custom-trained model) to analyze logs and
 * generate incident reports.
 *
 * For this demonstration, the GenAI logic is simplified using string matching
 * on log messages to simulate detection, explanation, and resolution suggestions.
 */
@Service
public class GenAIService {

    /**
     * Analyzes a given log entry to detect, explain, and suggest resolution for potential incidents.
     *
     * @param logEntry The log entry to analyze.
     * @return An IncidentReport containing the analysis results.
     */
    public IncidentReport analyzeLog(LogEntry logEntry) {
        String message = logEntry.getMessage() != null ? logEntry.getMessage().toLowerCase() : "";
        String serviceName = logEntry.getServiceName() != null ? logEntry.getServiceName() : "UnknownService";
        Instant currentTimestamp = Instant.now();
        String reportId = UUID.randomUUID().toString();

        // Initialize a default "no incident" report
        IncidentReport report = new IncidentReport(
            reportId,
            currentTimestamp,
            false,
            "No Incident",
            "No critical incident detected based on current log analysis.",
            "The log entry appears to be routine or informational.",
            "Continue monitoring system logs and metrics."
        );

        // --- Simulated GenAI Logic based on log message content ---

        if (message.contains("outofmemoryerror")) {
            report.setIsIncident(true);
            report.setType("Memory Leak/Exhaustion");
            report.setDescription("Application " + serviceName + " is experiencing OutOfMemoryError, likely due to heap exhaustion.");
            report.setExplanation("A 'java.lang.OutOfMemoryError' indicates that the Java Virtual Machine (JVM) has run out of memory. This can be caused by a memory leak (objects not being garbage collected), excessive object creation, or an undersized heap for the application's workload.");
            report.setResolutionSteps("1. Analyze heap dump to identify memory leaks (e.g., using jmap and Eclipse MAT).\n" +
                                      "2. Increase JVM heap size (-Xmx parameter).\n" +
                                      "3. Review recent code changes for memory-intensive operations.\n" +
                                      "4. Check for unclosed resources (e.g., database connections, file streams).");
        } else if (message.contains("nullpointerexception")) {
            report.setIsIncident(true);
            report.setType("Application Error: Null Pointer");
            report.setDescription("Application " + serviceName + " encountered a NullPointerException, indicating a programming error.");
            report.setExplanation("A 'NullPointerException' occurs when an application attempts to use an object reference that has not been initialized (i.e., it's null). This is a common runtime error in Java and usually points to a logical flaw in the code.");
            report.setResolutionSteps("1. Identify the exact line of code from the stack trace provided in the log.\n" +
                                      "2. Debug the code to understand why the object reference is null at that point.\n" +
                                      "3. Add null checks where necessary or ensure proper object initialization.\n" +
                                      "4. Deploy a fix and monitor for recurrence.");
        } else if (message.contains("timeout") || message.contains("timed out") || message.contains("connection refused")) {
            report.setIsIncident(true);
            report.setType("External Service/Network Issue");
            report.setDescription("Application " + serviceName + " is experiencing connectivity or timeout issues with an external dependency.");
            report.setExplanation("Timeouts or connection refused errors often indicate problems communicating with external services (databases, APIs, message queues) or underlying network infrastructure issues. This can lead to degraded performance or service unavailability.");
            report.setResolutionSteps("1. Verify the status of the external service/dependency.\n" +
                                      "2. Check network connectivity between the application and the dependency.\n" +
                                      "3. Review firewall rules or security group configurations.\n" +
                                      "4. Examine application configuration for correct service endpoints and credentials.\n" +
                                      "5. Consider implementing retry mechanisms with exponential backoff.");
        } else if (message.contains("error") && logEntry.getLevel() != null && logEntry.getLevel().equalsIgnoreCase("ERROR")) {
            // Generic error handling if specific patterns are not matched
            report.setIsIncident(true);
            report.setType("Generic Application Error");
            report.setDescription("Application " + serviceName + " reported a general error that requires investigation.");
            report.setExplanation("An 'ERROR' level log indicates an issue that prevents normal operation. Further context is needed to pinpoint the exact cause.");
            report.setResolutionSteps("1. Review surrounding log entries for more context.\n" +
                                      "2. Check application health and resource utilization (CPU, memory, disk I/O).\n" +
                                      "3. Consult monitoring dashboards for anomalies around the time of the error.\n" +
                                      "4. If recurring, consider enabling more verbose logging for the affected component.");
        }

        // In a real GenAI setup, you would construct a prompt like:
        // "Analyze the following Java application log entry: '{logEntry.getMessage()}'.
        // Is this indicative of an incident? If yes, provide a type, a brief description,
        // a detailed explanation of the likely root cause, and concrete resolution steps.
        // Format your response as a JSON object with fields: isIncident, type, description, explanation, resolutionSteps."
        // And then parse the LLM's JSON response.

        return report;
    }
}