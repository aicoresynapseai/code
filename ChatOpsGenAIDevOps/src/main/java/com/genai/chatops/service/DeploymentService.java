package com.genai.chatops.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

@Service
@Slf4j
public class DeploymentService {

    @Value("${deployment.script.path}")
    private String deploymentScriptPath;

    /**
     * Simulates the deployment of a Java application to a specified environment and version.
     * Executes an external shell script and provides real-time output via a consumer.
     *
     * @param environment The target environment (e.g., "dev", "staging", "production").
     * @param version The application version (e.g., "latest", "v1.0.0").
     * @param outputConsumer A consumer to handle real-time output lines from the script.
     * @return A CompletableFuture that completes with true if the deployment script exits successfully, false otherwise.
     */
    public CompletableFuture<Boolean> deployApplication(String environment, String version, Consumer<String> outputConsumer) {
        log.info("Attempting to deploy application: env={}, version={}", environment, version);

        return CompletableFuture.supplyAsync(() -> {
            try {
                File scriptFile = new File(deploymentScriptPath);
                if (!scriptFile.exists()) {
                    String errorMessage = "Deployment script not found at: " + deploymentScriptPath;
                    outputConsumer.accept("ERROR: " + errorMessage);
                    log.error(errorMessage);
                    return false;
                }
                // Make sure the script is executable on Unix-like systems
                if (!scriptFile.canExecute()) {
                    boolean success = scriptFile.setExecutable(true);
                    if (!success) {
                        String errorMessage = "Failed to make deployment script executable: " + deploymentScriptPath;
                        outputConsumer.accept("ERROR: " + errorMessage);
                        log.error(errorMessage);
                        return false;
                    }
                    log.info("Made deployment script executable: {}", deploymentScriptPath);
                }

                // Build the process command based on OS
                ProcessBuilder processBuilder;
                if (System.getProperty("os.name").toLowerCase().contains("win")) {
                    // For Windows, execute .bat or .cmd files directly, or use cmd /c for shell scripts
                    // Assuming deploy_java_app.sh would be renamed to .bat or cmd on Windows for this example.
                    // Or, if Git Bash is installed, you could try "bash.exe deploy_java_app.sh"
                    outputConsumer.accept("Starting deployment on Windows...");
                    processBuilder = new ProcessBuilder("cmd.exe", "/c", deploymentScriptPath, environment, version);
                } else {
                    // For Linux/macOS
                    outputConsumer.accept("Starting deployment on Unix-like system...");
                    processBuilder = new ProcessBuilder("bash", deploymentScriptPath, environment, version);
                }

                Process process = processBuilder.start();

                // Read output from the script in real-time
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    outputConsumer.accept(line);
                    log.info("[SCRIPT OUTPUT] {}", line); // Log script output to application logs
                }

                // Read error output
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                while ((line = errorReader.readLine()) != null) {
                    outputConsumer.accept("[ERROR] " + line);
                    log.error("[SCRIPT ERROR] {}", line); // Log script errors to application logs
                }

                int exitCode = process.waitFor();
                if (exitCode == 0) {
                    outputConsumer.accept("Deployment script finished successfully! Exit Code: " + exitCode);
                    log.info("Deployment script for env {} version {} completed successfully.", environment, version);
                    return true;
                } else {
                    outputConsumer.accept("Deployment script failed with Exit Code: " + exitCode);
                    log.error("Deployment script for env {} version {} failed with Exit Code: {}", environment, version, exitCode);
                    return false;
                }
            } catch (Exception e) {
                String errorMessage = "Exception during deployment: " + e.getMessage();
                outputConsumer.accept("EXCEPTION: " + errorMessage);
                log.error(errorMessage, e);
                return false;
            }
        });
    }

    /**
     * Simulates fetching the deployment status for a given environment.
     * In a real scenario, this would query a monitoring system or CI/CD tool.
     *
     * @param environment The environment to check status for.
     * @return A string indicating the simulated status.
     */
    public String getDeploymentStatus(String environment) {
        log.info("Checking simulated deployment status for environment: {}", environment);
        // Simulate different statuses based on environment for demo purposes
        if ("production".equalsIgnoreCase(environment)) {
            return "Production environment is stable and running version 1.5.0.";
        } else if ("staging".equalsIgnoreCase(environment)) {
            return "Staging environment is active with the latest build, currently undergoing QA checks.";
        } else if ("dev".equalsIgnoreCase(environment)) {
            return "Development environment is highly active with frequent changes. Last deployed: 5 minutes ago.";
        } else {
            return "Status for environment '" + environment + "' is unknown. Please specify 'dev', 'staging', or 'production'.";
        }
    }

    /**
     * Simulates rolling back a deployment.
     *
     * @param environment The environment to rollback.
     * @param version The version to rollback to.
     * @return A string indicating the simulated rollback status.
     */
    public String rollbackDeployment(String environment, String version) {
        log.info("Initiating simulated rollback for environment: {} to version: {}", environment, version);
        // In a real scenario, this would trigger a rollback mechanism in your CI/CD.
        return String.format("Simulated rollback initiated for %s to version %s. Please monitor logs for completion.", environment, version);
    }
}