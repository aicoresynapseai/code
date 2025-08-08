package com.aisentinel.security.interceptor;

import com.aisentinel.security.ai.AnomalyDetectionService;
import com.aisentinel.security.model.AttackPattern;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;
import java.util.Optional;

/**
 * Custom Spring MVC interceptor to integrate the AI-powered security check.
 * This interceptor will be executed before the request reaches the controller.
 * It uses the AnomalyDetectionService to scan for threats.
 */
@Component
public class AiSecurityInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AiSecurityInterceptor.class);

    private final AnomalyDetectionService anomalyDetectionService;

    // Constructor injection for the AnomalyDetectionService
    public AiSecurityInterceptor(AnomalyDetectionService anomalyDetectionService) {
        this.anomalyDetectionService = anomalyDetectionService;
    }

    /**
     * This method is called before the actual handler (controller method) is executed.
     * It's the primary point where our AI Guardian performs its pre-emptive check.
     *
     * @param request The current HTTP request.
     * @param response The current HTTP response.
     * @param handler The handler (Controller method) that will be executed.
     * @return true if the request should proceed to the handler, false otherwise.
     * @throws Exception If an error occurs during processing.
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("AI Security Interceptor: Pre-handling request to URI: {}", request.getRequestURI());

        // Call the AnomalyDetectionService to scan the request using the AI engine.
        Optional<AttackPattern> detectedThreat = anomalyDetectionService.scanForAnomalies(request);

        if (detectedThreat.isPresent()) {
            AttackPattern threat = detectedThreat.get();
            log.warn("AI Guardian BLOCKED request: Type='{}', Severity='{}', Description='{}', URI='{}'",
                     threat.getType(), threat.getSeverity(), threat.getDescription(), request.getRequestURI());

            // If a threat is detected, block the request and send an appropriate error response.
            sendErrorResponse(response, HttpStatus.FORBIDDEN, "Access Denied by AI Guardian: " + threat.getDescription());
            return false; // Stop the request processing chain
        }

        log.info("AI Guardian APPROVED request for URI: {}", request.getRequestURI());
        return true; // Allow the request to proceed to the controller
    }

    /**
     * Helper method to send an error response when a threat is detected.
     * @param response HttpServletResponse object.
     * @param status The HTTP status to set.
     * @param message The error message to send in the response body.
     * @throws IOException If writing to the response output stream fails.
     */
    private void sendErrorResponse(HttpServletResponse response, HttpStatus status, String message) throws IOException {
        response.setStatus(status.value()); // Set the HTTP status code (e.g., 403 Forbidden)
        response.setContentType("application/json"); // Set content type for the error message
        // You could return a more structured error object (e.g., JSON) here
        response.getWriter().write("{\"status\":" + status.value() + ", \"error\":\"" + message + "\"}");
        response.getWriter().flush();
    }

    // postHandle and afterCompletion methods can be used for post-processing or cleanup,
    // but are not critical for the AI Guardian's primary blocking function.
    // @Override
    // public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    //     log.debug("AI Security Interceptor: Post-handling request to URI: {}", request.getRequestURI());
    // }

    // @Override
    // public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    //     log.debug("AI Security Interceptor: After completion for URI: {}", request.getRequestURI());
    // }
}