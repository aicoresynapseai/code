package com.aisentinel.security.ai;

import com.aisentinel.security.model.AttackPattern;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * Service responsible for orchestrating the anomaly detection process using the AI Security Engine.
 * This acts as a bridge between the HTTP request context and the AI analysis logic.
 */
@Service
public class AnomalyDetectionService {

    private final AiSecurityEngine aiSecurityEngine;

    // Constructor injection for AiSecurityEngine
    public AnomalyDetectionService(AiSecurityEngine aiSecurityEngine) {
        this.aiSecurityEngine = aiSecurityEngine;
    }

    /**
     * Scans an incoming HTTP request for anomalies or known attack patterns using the AI engine.
     * It extracts relevant information from the request and passes it to the AI for analysis.
     *
     * @param request The HttpServletRequest object representing the incoming request.
     * @return An Optional containing an AttackPattern if a threat is detected, otherwise empty.
     */
    public Optional<AttackPattern> scanForAnomalies(HttpServletRequest request) {
        // Build a comprehensive string from request details for AI analysis.
        // In a real scenario, this would be more structured (JSON, protobuf)
        // and include more detailed metrics (IP reputation, user behavior, etc.).
        StringBuilder requestData = new StringBuilder();
        requestData.append("Method: ").append(request.getMethod()).append("\n");
        requestData.append("URI: ").append(request.getRequestURI()).append("\n");
        requestData.append("QueryString: ").append(request.getQueryString()).append("\n");
        requestData.append("Headers: ").append("\n");
        request.getHeaderNames().asIterator().forEachRemaining(headerName ->
            requestData.append("  ").append(headerName).append(": ").append(request.getHeader(headerName)).append("\n")
        );

        // Attempt to read the request body.
        // Note: Reading HttpServletRequest input stream directly can consume it,
        // preventing subsequent reads by the controller.
        // For production, consider using a filter that wraps the request to allow multiple reads.
        try {
            String body = StreamUtils.copyToString(request.getInputStream(), StandardCharsets.UTF_8);
            requestData.append("Body: ").append(body).append("\n");
        } catch (IOException e) {
            // Log the error but don't fail the request entirely just because body couldn't be read.
            // This might happen if the body has already been consumed by another filter.
            System.err.println("Could not read request body for AI analysis: " + e.getMessage());
            requestData.append("Body: <unavailable>\n");
        }

        // Pass the aggregated request data to the simulated AI engine for analysis.
        return aiSecurityEngine.analyzeRequest(requestData.toString());
    }
}