package com.devopsai.predictive;

import com.devopsai.predictive.model.BuildData;
import com.devopsai.predictive.service.HistoricalDataLoader;
import com.devopsai.predictive.service.PredictionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

/**
 * Main application class for the CI-CD-AI-Forecaster.
 * This class orchestrates the loading of historical data and the execution of predictive analysis.
 */
public class PipelineFailurePredictorApp {

    private static final Logger logger = LoggerFactory.getLogger(PipelineFailurePredictorApp.class);
    private static final String CONFIG_FILE = "application.properties";
    private static final String HISTORICAL_DATA_CSV_PATH = "data/historical_builds.csv";

    public static void main(String[] args) {
        logger.info("Starting CI-CD-AI-Forecaster application...");

        // Load application properties (e.g., for API keys or thresholds)
        Properties properties = loadApplicationProperties();
        // You would typically retrieve a GenAI API key here:
        // String genAiApiKey = properties.getProperty("genai.api.key", "YOUR_GENAI_API_KEY_HERE");
        // logger.info("Using GenAI API Key (first 5 chars): {}", genAiApiKey.substring(0, Math.min(genAiApiKey.length(), 5)));

        // 1. Load historical CI/CD build data
        HistoricalDataLoader dataLoader = new HistoricalDataLoader(HISTORICAL_DATA_CSV_PATH);
        List<BuildData> historicalBuilds = dataLoader.loadData();

        if (historicalBuilds.isEmpty()) {
            logger.warn("No historical build data loaded. Cannot perform prediction.");
            return;
        }

        // 2. Perform predictive analysis using the PredictionService
        // This is where the conceptual GenAI integration happens.
        PredictionService predictionService = new PredictionService();
        String analysisReport = predictionService.analyzeAndPredict(historicalBuilds);

        // 3. Output the prediction report
        logger.info("\nGenerated Predictive Analysis Report:\n{}", analysisReport);

        logger.info("CI-CD-AI-Forecaster application finished.");
    }

    /**
     * Loads application properties from application.properties file.
     * @return Properties object containing loaded configurations.
     */
    private static Properties loadApplicationProperties() {
        Properties properties = new Properties();
        try (InputStream input = PipelineFailurePredictorApp.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input == null) {
                logger.warn("Sorry, unable to find {}. Using default configurations.", CONFIG_FILE);
                // Set default properties if file is not found
                properties.setProperty("genai.api.key", "default_api_key");
            } else {
                properties.load(input);
                logger.info("Loaded application properties from {}", CONFIG_FILE);
            }
        } catch (IOException ex) {
            logger.error("Error loading application properties from {}: {}", CONFIG_FILE, ex.getMessage());
            // Set default properties on error
            properties.setProperty("genai.api.key", "default_api_key");
        }
        return properties;
    }
}