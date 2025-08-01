package com.genaidoc.pipeline;

import com.genaidoc.pipeline.config.ApplicationConfig;

/**
 * Main application class for the Automated Documentation Generation project.
 * This class simulates a simple Java service that processes data.
 */
public class App {

    private final ApplicationConfig config;

    /**
     * Constructs the App with a given configuration.
     * @param config The application configuration instance.
     */
    public App(ApplicationConfig config) {
        this.config = config;
    }

    /**
     * The main entry point of the application.
     * Initializes the application and performs a sample operation.
     * @param args Command line arguments (not used in this example).
     */
    public static void main(String[] args) {
        // Create an instance of the application configuration
        ApplicationConfig appConfig = new ApplicationConfig("default_service", 5000);
        // Create an instance of the App using the configuration
        App application = new App(appConfig);
        // Perform a sample data processing operation
        application.processData("Sample input data");
    }

    /**
     * Simulates processing of input data.
     * This method could represent any business logic, like data transformation,
     * database interaction, or API calls.
     * @param data The string data to be processed.
     * @return A processed string, demonstrating some transformation.
     */
    public String processData(String data) {
        // Log the received data and the active service name from configuration
        System.out.println("Processing data: \"" + data + "\" using service: " + config.getServiceName());
        // Simulate data transformation
        String processedData = data.toUpperCase() + " - PROCESSED @ " + System.currentTimeMillis();
        System.out.println("Processed data: \"" + processedData + "\"");
        // Return the transformed data
        return processedData;
    }

    /**
     * Retrieves the current configuration used by the application.
     * @return The ApplicationConfig instance.
     */
    public ApplicationConfig getConfig() {
        return config;
    }
}