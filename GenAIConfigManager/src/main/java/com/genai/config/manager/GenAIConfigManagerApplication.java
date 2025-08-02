package com.genai.config.manager;

import com.fasterxml.jackson.databind.JsonNode;
import com.genai.config.manager.model.AppConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Main application class for the GenAI Configuration Manager demo.
 * Orchestrates the workflow: GenAI simulation -> Generation -> Validation -> Optimization -> Consumption.
 */
public class GenAIConfigManagerApplication {

    private static final String OUTPUT_DIR = "target"; // Directory for generated files
    private static final String GENERATED_YAML = OUTPUT_DIR + "/generated-application.yaml";
    private static final String GENERATED_PROPERTIES = OUTPUT_DIR + "/generated-application.properties";
    private static final String OPTIMIZED_YAML = OUTPUT_DIR + "/optimized-application.yaml";
    private static final String OPTIMIZED_PROPERTIES = OUTPUT_DIR + "/optimized-application.properties";

    public static void main(String[] args) {
        System.out.println("Starting GenAI Configuration Management Demo...");

        // 1. Initialize services
        GenAIIntegrationService genAIService = new GenAIIntegrationService();
        ConfigurationService configService = new ConfigurationService();

        try {
            // Ensure output directory exists and is clean for a fresh run
            Path outputDirPath = Paths.get(OUTPUT_DIR);
            if (Files.exists(outputDirPath)) {
                Files.walk(outputDirPath)
                        .filter(Files::isRegularFile)
                        .forEach(p -> {
                            try {
                                Files.delete(p);
                            } catch (IOException e) {
                                System.err.println("Could not delete old file: " + p + " - " + e.getMessage());
                            }
                        });
            } else {
                Files.createDirectories(outputDirPath);
            }


            // 2. Simulate GenAI output retrieval
            System.out.println("\n--- Step 1: Simulating GenAI Config Generation ---");
            JsonNode genAIResponseNode = genAIService.getMockGenAIConfigResponse();
            String genAIResponseString = genAIResponseNode.toString(); // Convert JsonNode to string for service

            // 3. Generate initial config files from GenAI output
            configService.generateConfigFiles(genAIResponseString, OUTPUT_DIR);

            // 4. Validate generated config files
            System.out.println("\n--- Step 2: Validating Generated Configurations ---");
            boolean yamlValid = configService.validateYamlConfig(GENERATED_YAML);
            boolean propertiesValid = configService.validatePropertiesConfig(GENERATED_PROPERTIES);

            if (!yamlValid || !propertiesValid) {
                System.err.println("Validation failed for one or more configurations. Aborting further steps.");
                return;
            }

            // 5. Optimize validated config files
            System.out.println("\n--- Step 3: Optimizing Configurations ---");
            configService.optimizeYamlConfig(GENERATED_YAML, OPTIMIZED_YAML);
            configService.optimizePropertiesConfig(GENERATED_PROPERTIES, OPTIMIZED_PROPERTIES);

            // 6. Consume the optimized config files in the application
            System.out.println("\n--- Step 4: Consuming Optimized Configurations ---");
            // Load and display AppConfig from optimized YAML
            AppConfig appConfig = configService.loadAppConfigFromYaml(OPTIMIZED_YAML);
            System.out.println("\nLoaded Application Config (YAML):");
            System.out.println(appConfig);

            // Load and display Properties from optimized .properties file
            Properties featureProps = configService.loadPropertiesConfig(OPTIMIZED_PROPERTIES);
            System.out.println("\nLoaded Feature Toggles (Properties):");
            featureProps.forEach((key, value) -> System.out.println("  " + key + "=" + value));

            System.out.println("\nGenAI Configuration Management Demo Finished Successfully.");

        } catch (IOException e) {
            System.err.println("An I/O error occurred during the process: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}