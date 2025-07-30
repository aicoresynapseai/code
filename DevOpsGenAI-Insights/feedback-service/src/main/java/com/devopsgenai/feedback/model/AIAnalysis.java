package com.devopsgenai.feedback.model;

import lombok.Data; // Lombok annotation to generate getters, setters, equals, hashCode, and toString methods
import java.util.List;

/**
 * Represents the AI-generated analysis and feedback for a build report.
 * This object is created by the `AIService` after processing a `BuildReport`.
 */
@Data // Automatically generates boilerplate code (getters, setters, etc.)
public class AIAnalysis {
    private String analysisSummary;     // A concise summary of the build issue/status
    private List<String> suggestedFixes; // Actionable suggestions to resolve issues
    private String confidenceScore;     // A simulated confidence score from the AI (e.g., "High", "Medium", "Low")
    private String aiModelUsed;         // The name of the AI model used (e.g., "SimulatedGenAI-v1.0")

    // Lombok's @Data annotation automatically provides public getters and setters
    // for all fields, a no-argument constructor, equals(), hashCode(), and toString() methods.
    // If Lombok is not used, these methods would need to be manually implemented.
}