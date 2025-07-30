package com.devopsgenai.feedback.controller;

import com.devopsgenai.feedback.model.BuildReport;
import com.devopsgenai.feedback.model.FullBuildDetails;
import com.devopsgenai.feedback.service.TelemetryService;
import org.springframework.stereotype.Controller; // Use @Controller for MVC (returning view names)
import org.springframework.ui.Model; // For passing data to Thymeleaf templates
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody; // For returning JSON responses
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * REST Controller for receiving build telemetry and serving the feedback dashboard.
 * This controller handles two main types of requests:
 * 1. POST requests to `/api/telemetry/build` for ingesting build reports.
 * 2. GET requests to `/dashboard` for displaying the web-based feedback dashboard.
 * 3. GET requests to `/api/reports` for programmatic access to the reports.
 */
@Controller // Indicates that this class is a Spring MVC controller
public class FeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);

    private final TelemetryService telemetryService;

    /**
     * Constructor for FeedbackController, injecting the TelemetryService dependency.
     * @param telemetryService The service responsible for processing telemetry.
     */
    public FeedbackController(TelemetryService telemetryService) {
        this.telemetryService = telemetryService;
    }

    /**
     * REST API endpoint for receiving build telemetry reports.
     * This method accepts a POST request with a JSON payload representing a BuildReport.
     *
     * @param report The BuildReport object automatically converted from the JSON request body.
     * @return A simple confirmation message string.
     */
    @PostMapping("/api/telemetry/build")
    @ResponseBody // Indicates that the return value should be bound directly to the web response body
    public String receiveBuildTelemetry(@RequestBody BuildReport report) {
        logger.info("API: Received build report for project: {} (Build ID: {})", report.getProjectId(), report.getBuildId());
        telemetryService.processBuildReport(report); // Process the report using the service
        return "Build report received and processed.";
    }

    /**
     * Web endpoint for displaying the real-time feedback dashboard.
     * This method renders the `dashboard.html` Thymeleaf template.
     *
     * @param model The Spring Model object to pass data to the view.
     * @return The name of the Thymeleaf template to render (e.g., "dashboard").
     */
    @GetMapping("/dashboard")
    public String showDashboard(Model model) {
        // Retrieve the history of build reports and AI analysis
        List<FullBuildDetails> buildHistory = telemetryService.getBuildHistory();
        model.addAttribute("builds", buildHistory); // Add the list to the model, accessible in Thymeleaf as `builds`
        logger.info("Web: Displaying dashboard with {} build reports.", buildHistory.size());
        return "dashboard"; // Refers to src/main/resources/templates/dashboard.html
    }

    /**
     * REST API endpoint to retrieve all stored build reports and their AI analysis in JSON format.
     * Useful for programmatic access to the dashboard data.
     *
     * @return A list of FullBuildDetails objects in JSON format.
     */
    @GetMapping("/api/reports")
    @ResponseBody
    public List<FullBuildDetails> getAllReports() {
        return telemetryService.getBuildHistory();
    }
}