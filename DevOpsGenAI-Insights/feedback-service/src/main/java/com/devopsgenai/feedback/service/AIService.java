package com.devopsgenai.feedback.service;

import com.devopsgenai.feedback.model.AIAnalysis;
import com.devopsgenai.feedback.model.BuildReport;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

/**
 * Service responsible for simulating Generative AI analysis of build reports.
 * In a real-world scenario, this service would make API calls to an actual
 * GenAI model (e.g., OpenAI's GPT, Google's Gemini, Anthropic's Claude).
 * For this demonstration, it uses rule-based logic to mimic AI behavior.
 */
@Service
public class AIService {

    /**
     * Analyzes a given build report and generates AI-like insights.
     *
     * @param report The BuildReport to analyze.
     * @return An AIAnalysis object containing summary, suggested fixes, and confidence.
     */
    public AIAnalysis analyzeBuildReport(BuildReport report) {
        AIAnalysis analysis = new AIAnalysis();
        List<String> suggestedFixes = new ArrayList<>();
        String summary;
        String confidence = "Medium"; // Default confidence

        if ("SUCCESS".equalsIgnoreCase(report.getStatus())) {
            // AI feedback for a successful build
            summary = "Build ID " + report.getBuildId() + " for project '" + report.getProjectId() + "' completed successfully. All " + report.getTotalTests() + " tests passed in " + String.format("%.2f", report.getBuildTimeSeconds()) + " seconds.";
            suggestedFixes.add("Maintain code quality standards.");
            suggestedFixes.add("Consider optimizing build time if it consistently increases.");
            confidence = "High";
        } else {
            // AI feedback for a failed build
            summary = "Build ID " + report.getBuildId() + " for project '" + report.getProjectId() + "' FAILED. " + report.getFailedTests() + " out of " + report.getTotalTests() + " tests failed.";

            // Simulate AI detecting common error patterns
            boolean hasNPE = report.getErrorMessages().stream().anyMatch(msg -> msg.contains("NullPointerException"));
            boolean hasAssertionError = report.getErrorMessages().stream().anyMatch(msg -> msg.contains("AssertionError") || msg.contains("Expected"));
            boolean hasArithmeticError = report.getErrorMessages().stream().anyMatch(msg -> msg.contains("ArithmeticException") || msg.contains("divide by zero"));

            if (hasNPE) {
                summary += " Critical NullPointerException detected.";
                suggestedFixes.add("Review stack trace for 'NullPointerException'. Focus on initialization of objects in the indicated classes (e.g., UserService).");
                suggestedFixes.add("Ensure all object references are non-null before dereferencing them. Implement null checks or Optional usage.");
                confidence = "High"; // AI is confident about NPE
            }
            if (hasAssertionError) {
                summary += " Test assertion failures indicate logic or data issues.";
                suggestedFixes.add("Examine failed test cases related to 'AssertionError'. Verify expected vs. actual values. Check data integrity or test setup.");
                suggestedFixes.add("If related to database, verify connection stability and query results (e.g., 'Expected 12 records but found 10').");
                confidence = "High"; // AI is confident about assertion errors
            }
            if (hasArithmeticError) {
                summary += " Arithmetic error, likely division by zero.";
                suggestedFixes.add("Check division operations for potential zero denominators. Implement input validation or error handling for such cases.");
                confidence = "High"; // AI is confident about arithmetic errors
            }

            // Generic suggestions if no specific pattern is found or as additional advice
            if (suggestedFixes.isEmpty()) {
                summary += " General build failure. Review error messages for clues.";
                suggestedFixes.add("Inspect full build logs and stack traces for detailed root cause analysis.");
                suggestedFixes.add("Consult with the team for recent code changes that might have introduced regressions.");
                confidence = "Low"; // Less confident if no specific pattern
            } else {
                suggestedFixes.add("Consider running relevant unit/integration tests locally to reproduce the issue.");
            }
        }

        analysis.setAnalysisSummary(summary);
        analysis.setSuggestedFixes(suggestedFixes);
        analysis.setConfidenceScore(confidence);
        analysis.setAiModelUsed("SimulatedGenAI-v1.0"); // Name of our simulated AI model

        return analysis;
    }
}