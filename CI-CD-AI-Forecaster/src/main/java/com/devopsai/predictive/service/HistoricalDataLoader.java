package com.devopsai.predictive.service;

import com.devopsai.predictive.model.BuildData;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to load historical build data from a CSV file.
 * In a real-world scenario, this would interface with CI/CD system APIs (e.g., Jenkins, GitLab, Azure DevOps)
 * to fetch actual build logs, metrics, and deployment statuses.
 */
public class HistoricalDataLoader {

    private static final Logger logger = LoggerFactory.getLogger(HistoricalDataLoader.class);
    private final String csvFilePath;

    public HistoricalDataLoader(String csvFilePath) {
        this.csvFilePath = csvFilePath;
    }

    /**
     * Loads build data from the configured CSV file.
     *
     * @return A list of BuildData objects.
     */
    public List<BuildData> loadData() {
        List<BuildData> buildDataList = new ArrayList<>();

        // Use try-with-resources to ensure the reader is closed
        try (Reader in = new InputStreamReader(getClass().getClassLoader().getResourceAsStream(csvFilePath))) {
            if (in == null) {
                logger.error("CSV file not found: {}", csvFilePath);
                return buildDataList;
            }

            // Define CSV format with header auto-detection and trimming
            CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                    .setHeader() // Auto-detect headers from the first line
                    .setSkipHeaderRecord(true) // Skip the header record for parsing data
                    .setTrim(true) // Trim spaces from values
                    .build();

            // Parse the CSV file
            CSVParser parser = new CSVParser(in, csvFormat);
            for (CSVRecord record : parser) {
                try {
                    String buildId = record.get("buildId");
                    String timestamp = record.get("timestamp");
                    int durationSeconds = Integer.parseInt(record.get("durationSeconds"));
                    String status = record.get("status");
                    int testCount = Integer.parseInt(record.get("testCount"));
                    int failedTests = Integer.parseInt(record.get("failedTests"));
                    boolean deploymentSuccess = Boolean.parseBoolean(record.get("deploymentSuccess"));
                    String errorMessage = record.get("errorMessage"); // Can be empty if no error

                    BuildData buildData = new BuildData(
                            buildId,
                            timestamp,
                            durationSeconds,
                            status,
                            testCount,
                            failedTests,
                            deploymentSuccess,
                            errorMessage
                    );
                    buildDataList.add(buildData);
                } catch (Exception e) {
                    logger.warn("Skipping malformed CSV record: {}. Error: {}", record, e.getMessage());
                }
            }
            logger.info("Successfully loaded {} historical build records from {}", buildDataList.size(), csvFilePath);

        } catch (IOException e) {
            logger.error("Error loading historical build data from {}: {}", csvFilePath, e.getMessage(), e);
        }
        return buildDataList;
    }
}