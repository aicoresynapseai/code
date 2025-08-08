package com.aisentinel.security.ai;

import com.aisentinel.security.model.AttackPattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * This class simulates an AI security engine.
 * In a real-world scenario, this would be backed by actual machine learning models
 * that analyze patterns, detect anomalies, and classify threats.
 * For this example, it uses simple pattern matching to simulate threat detection.
 */
@Component
public class AiSecurityEngine {

    private static final Logger log = LoggerFactory.getLogger(AiSecurityEngine.class);

    // Simulated "known bad patterns" that an AI model might have learned.
    // This could be a database of attack signatures, anomaly profiles, etc.
    private final List<AttackPattern> knownThreatPatterns = Arrays.asList(
        new AttackPattern("SQL_INJECTION", "Highly Critical", "SQL injection attempt detected."),
        new AttackPattern("XSS_ATTACK", "Critical", "Cross-site scripting payload detected."),
        new AttackPattern("PATH_TRAVERSAL", "High", "Directory traversal attempt detected."),
        new AttackPattern("BRUTE_FORCE", "Medium", "Suspicious login pattern indicating brute force."),
        new AttackPattern("ANOMALOUS_PAYLOAD_SIZE", "Low", "Unusually large request payload detected.")
    );

    /**
     * Simulates the AI's analysis of a request payload for security threats.
     * In a real system, this would involve complex ML inference.
     *
     * @param requestPayload The full request content (e.g., body, query parameters, headers concatenated).
     * @return An Optional containing an AttackPattern if a threat is detected, otherwise empty.
     */
    public Optional<AttackPattern> analyzeRequest(String requestPayload) {
        if (requestPayload == null || requestPayload.isEmpty()) {
            return Optional.empty();
        }

        log.info("AI Security Engine analyzing request payload: {}", requestPayload.length() > 100 ? requestPayload.substring(0, 100) + "..." : requestPayload);

        // Simulate pattern matching based on "learned" threats
        // In a real AI, this would be a probabilistic classification.
        for (AttackPattern pattern : knownThreatPatterns) {
            if (requestPayload.contains(pattern.getType().replace("_", " ")) || // e.g., "SQL INJECTION"
                requestPayload.toLowerCase().contains("select * from") ||
                requestPayload.toLowerCase().contains("union all select") ||
                requestPayload.toLowerCase().contains("<script>") ||
                requestPayload.toLowerCase().contains("../..") ||
                (pattern.getType().equals("ANOMALOUS_PAYLOAD_SIZE") && requestPayload.length() > 500) // Simple size anomaly
            ) {
                log.warn("Threat detected by AI: Type='{}', Description='{}'", pattern.getType(), pattern.getDescription());
                return Optional.of(pattern);
            }
        }

        log.debug("No immediate threats detected by AI for this request.");
        return Optional.empty();
    }
}