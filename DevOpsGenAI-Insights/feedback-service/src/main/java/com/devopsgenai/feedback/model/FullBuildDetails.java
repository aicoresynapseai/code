package com.devopsgenai.feedback.model;

import lombok.Data; // Lombok annotation for boilerplate code generation
import java.time.LocalDateTime; // To record when the report was received

/**
 * A composite model combining the raw BuildReport with its AI-generated analysis.
 * This is what will be stored in memory and displayed on the dashboard.
 */
@Data // Automatically generates getters, setters, equals, hashCode, and toString
public class FullBuildDetails {
    private BuildReport buildReport;    // The original build telemetry
    private AIAnalysis aiAnalysis;      // The AI's insights for this build
    private LocalDateTime timestamp;    // When this report was received and processed

    public FullBuildDetails(BuildReport buildReport, AIAnalysis aiAnalysis) {
        this.buildReport = buildReport;
        this.aiAnalysis = aiAnalysis;
        this.timestamp = LocalDateTime.now(); // Set the current time upon creation
    }

    // Lombok's @Data handles the rest of the standard methods.
}