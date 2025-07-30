package com.devopsgenai.feedback.service;

import com.devopsgenai.feedback.model.AIAnalysis;
import com.devopsgenai.feedback.model.BuildReport;
import com.devopsgenai.feedback.model.FullBuildDetails;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Service responsible for processing incoming build telemetry.
 * It coordinates with the AIService to get insights and stores the
 * full build details for display on the dashboard.
 * In a real application, data would be persisted to a database.
 */
@Service
public class TelemetryService {

    private static final Logger logger = LoggerFactory.getLogger(TelemetryService.class);

    private final AIService aiService;
    // In-memory storage for demo purposes. In production, use a database (e.g., PostgreSQL, MongoDB).
    private final List<FullBuildDetails> buildHistory = Collections.synchronizedList(new ArrayList<>());

    /**
     * Constructor for TelemetryService, injecting the AIService dependency.
     * @param aiService The AI service to use for analysis.
     */
    public TelemetryService(AIService aiService) {
        this.aiService = aiService;
    }

    /**
     * Processes an incoming build report:
     * 1. Logs the report.
     * 2. Calls the AI service for analysis.
     * 3. Stores the combined report and analysis.
     * 4. Simulates sending feedback (e.g., to a bot).
     *
     * @param report The BuildReport received from the CI/CD pipeline.
     */
    public void processBuildReport(BuildReport report) {
        logger.info("Received Build Report: ProjectId={}, BuildId={}, Status={}, FailedTests={}",
                report.getProjectId(), report.getBuildId(), report.getStatus(), report.getFailedTests());

        // 1. Perform AI analysis on the build report
        AIAnalysis aiAnalysis = aiService.analyzeBuildReport(report);

        // 2. Store the full details (report + analysis)
        FullBuildDetails fullDetails = new FullBuildDetails(report, aiAnalysis);
        buildHistory.add(0, fullDetails); // Add to the beginning to show latest first
        // Keep history size manageable for demo
        if (buildHistory.size() > 10) {
            buildHistory.remove(buildHistory.size() - 1);
        }

        // 3. Simulate real-time feedback (e.g., to a bot, dashboard update)
        logger.info("--- AI Feedback for Build ID {} (Project: {}) ---", report.getBuildId(), report.getProjectId());
        logger.info("Summary: {}", aiAnalysis.getAnalysisSummary());
        logger.info("Suggested Fixes:");
        aiAnalysis.getSuggestedFixes().forEach(fix -> logger.info("  - {}", fix));
        logger.info("Confidence: {} (AI Model: {})", aiAnalysis.getConfidenceScore(), aiAnalysis.getAiModelUsed());
        logger.info("-----------------------------------------------------");
    }

    /**
     * Retrieves the history of all processed build details.
     *
     * @return An unmodifiable list of FullBuildDetails, ordered from newest to oldest.
     */
    public List<FullBuildDetails> getBuildHistory() {
        return Collections.unmodifiableList(buildHistory);
    }
}