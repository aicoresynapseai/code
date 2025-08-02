package com.genai.config.manager;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genai.config.manager.model.AppConfig;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Service responsible for generating, validating, and optimizing
 * application configuration files (YAML and .properties).
 */
public class ConfigurationService {

    private final Yaml yaml;
    private final ObjectMapper objectMapper;

    public ConfigurationService() {
        // Configure SnakeYAML for pretty printing YAML
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK); // Use block style for readability
        options.setPrettyFlow(true); // Pretty print maps and collections
        options.setIndent(2); // Set indentation to 2 spaces
        this.yaml = new Yaml(options);
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Generates configuration files from GenAI's proposed content.
     *
     * @param genAIResponse The JSON response from GenAI containing config strings.
     * @param outputDir     The directory where generated files will be saved.
     * @return A map of generated file paths and their content.
     * @throws IOException If there's an error writing the files.
     */
    public Map<String, String> generateConfigFiles(String genAIResponse, String outputDir) throws IOException {
        Map<String, String> generatedContent = new TreeMap<>();
        Path outputDirPath = Paths.get(outputDir);
        Files.createDirectories(outputDirPath); // Ensure output directory exists

        try {
            // Parse the GenAI JSON response to extract config strings
            JsonGenAIResponse parsedResponse = objectMapper.readValue(genAIResponse, JsonGenAIResponse.class);

            // Generate YAML file
            String yamlContent = parsedResponse.getProposedYaml();
            if (yamlContent != null && !yamlContent.trim().isEmpty()) {
                Path yamlPath = outputDirPath.resolve("generated-application.yaml");
                Files.writeString(yamlPath, yamlContent);
                generatedContent.put(yamlPath.toString(), yamlContent);
                System.out.println("Generated YAML file: " + yamlPath);
            }

            // Generate Properties file
            String propertiesContent = parsedResponse.getProposedProperties();
            if (propertiesContent != null && !propertiesContent.trim().isEmpty()) {
                Path propsPath = outputDirPath.resolve("generated-application.properties");
                Files.writeString(propsPath, propertiesContent);
                generatedContent.put(propsPath.toString(), propertiesContent);
                System.out.println("Generated Properties file: " + propsPath);
            }

        } catch (JsonProcessingException e) {
            System.err.println("Error parsing GenAI response JSON: " + e.getMessage());
            throw new IOException("Failed to parse GenAI response", e);
        }
        return generatedContent;
    }

    /**
     * Validates a YAML configuration file based on basic structural checks.
     * In a real scenario, this would involve a more robust schema validation library.
     *
     * @param yamlFilePath The path to the YAML file.
     * @return True if validation passes, false otherwise.
     */
    public boolean validateYamlConfig(String yamlFilePath) {
        System.out.println("\nValidating YAML file: " + yamlFilePath);
        try (FileReader reader = new FileReader(yamlFilePath)) {
            // Load YAML into a generic Map
            Map<String, Object> yamlMap = yaml.load(reader);

            if (yamlMap == null || yamlMap.isEmpty()) {
                System.err.println("Validation Error: YAML file is empty or invalid.");
                return false;
            }

            // Basic checks for required fields
            if (!yamlMap.containsKey("app-name") || !(yamlMap.get("app-name") instanceof String)) {
                System.err.println("Validation Error: 'app-name' field is missing or not a string.");
                return false;
            }
            if (!yamlMap.containsKey("environment") || !(yamlMap.get("environment") instanceof String)) {
                System.err.println("Validation Error: 'environment' field is missing or not a string.");
                return false;
            }

            // Check database section
            if (!yamlMap.containsKey("database") || !(yamlMap.get("database") instanceof Map)) {
                System.err.println("Validation Error: 'database' section is missing or malformed.");
                return false;
            }
            Map<String, Object> dbConfig = (Map<String, Object>) yamlMap.get("database");
            if (!dbConfig.containsKey("url") || !(dbConfig.get("url") instanceof String) ||
                !dbConfig.containsKey("username") || !(dbConfig.get("username") instanceof String) ||
                !dbConfig.containsKey("password") || !(dbConfig.get("password") instanceof String)) {
                System.err.println("Validation Error: Database credentials (url, username, password) are missing or malformed.");
                return false;
            }

            // Attempt to load into AppConfig POJO for more structured validation
            // Note: This requires the YAML structure to match AppConfig's fields (case-insensitive or using @JsonProperty)
            // For simple SnakeYAML default mapping, field names should match.
            // A custom constructor or mapping can be used for more flexibility.
            try (FileReader readerForPojo = new FileReader(yamlFilePath)) {
                Yaml pojoYaml = new Yaml(new Constructor(AppConfig.class)); // Create a new Yaml instance with specific constructor
                AppConfig appConfig = pojoYaml.load(readerForPojo);

                if (appConfig == null) {
                    System.err.println("Validation Error: Could not map YAML to AppConfig object.");
                    return false;
                }
                if (appConfig.getAppName() == null || appConfig.getAppName().isEmpty()) {
                    System.err.println("Validation Error: App name is empty after POJO mapping.");
                    return false;
                }
                if (appConfig.getDatabase() == null || appConfig.getDatabase().getUrl() == null || appConfig.getDatabase().getUrl().isEmpty()) {
                    System.err.println("Validation Error: Database URL is empty after POJO mapping.");
                    return false;
                }
                // Add more POJO-level checks here, e.g., URL format validation

            } catch (Exception e) {
                System.err.println("Validation Error: Failed to parse YAML into AppConfig POJO. " + e.getMessage());
                return false;
            }


            System.out.println("YAML validation successful.");
            return true;
        } catch (IOException e) {
            System.err.println("Validation Error: Could not read YAML file " + yamlFilePath + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Validation Error: An unexpected error occurred during YAML validation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Validates a .properties configuration file.
     * Checks for the presence of specific keys and basic format.
     *
     * @param propertiesFilePath The path to the .properties file.
     * @return True if validation passes, false otherwise.
     */
    public boolean validatePropertiesConfig(String propertiesFilePath) {
        System.out.println("\nValidating Properties file: " + propertiesFilePath);
        Properties props = new Properties();
        try (FileReader reader = new FileReader(propertiesFilePath)) {
            props.load(reader);

            if (props.isEmpty()) {
                System.err.println("Validation Error: Properties file is empty or invalid.");
                return false;
            }

            // Basic checks for required feature toggles
            if (!props.containsKey("feature.newUI")) {
                System.err.println("Validation Error: Missing 'feature.newUI' property.");
                return false;
            }
            if (!props.containsKey("feature.betaAnalytics")) {
                System.err.println("Validation Error: Missing 'feature.betaAnalytics' property.");
                return false;
            }

            // Check if values are boolean-like (true/false)
            String newUIRaw = props.getProperty("feature.newUI");
            String betaAnalyticsRaw = props.getProperty("feature.betaAnalytics");

            if (!("true".equalsIgnoreCase(newUIRaw) || "false".equalsIgnoreCase(newUIRaw))) {
                System.err.println("Validation Error: 'feature.newUI' value is not a valid boolean.");
                return false;
            }
            if (!("true".equalsIgnoreCase(betaAnalyticsRaw) || "false".equalsIgnoreCase(betaAnalyticsRaw))) {
                System.err.println("Validation Error: 'feature.betaAnalytics' value is not a valid boolean.");
                return false;
            }

            System.out.println("Properties validation successful.");
            return true;
        } catch (IOException e) {
            System.err.println("Validation Error: Could not read Properties file " + propertiesFilePath + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Validation Error: An unexpected error occurred during Properties validation: " + e.getMessage());
            return false;
        }
    }

    /**
     * Optimizes a YAML configuration file.
     * Current optimization: re-writes YAML using SnakeYAML's pretty-print, which standardizes format.
     * Also, it conceptually shows how to remove unused keys if a schema comparison were done.
     *
     * @param inputFilePath  The path to the input YAML file.
     * @param outputFilePath The path where the optimized YAML file will be saved.
     * @return True if optimization is successful, false otherwise.
     */
    public boolean optimizeYamlConfig(String inputFilePath, String outputFilePath) {
        System.out.println("\nOptimizing YAML file: " + inputFilePath + " -> " + outputFilePath);
        try (FileReader reader = new FileReader(inputFilePath);
             FileWriter writer = new FileWriter(outputFilePath)) {

            // Load the YAML into a generic Map
            Map<String, Object> yamlMap = yaml.load(reader);

            if (yamlMap == null || yamlMap.isEmpty()) {
                System.err.println("Optimization Warning: YAML file is empty. Skipping optimization.");
                return false;
            }

            // Example of a simple optimization:
            // 1. Sort top-level keys for consistency (SnakeYAML naturally does this on dumping maps)
            // 2. Remove conceptual "unused" keys (demonstrative, requires knowledge of actual usage)
            // For a real scenario, you'd compare against a canonical schema or usage analysis.
            yamlMap.remove("unused-key-example"); // Simulate removing an unused key

            // Re-dump the map to the output file using pretty-print options
            yaml.dump(yamlMap, writer);
            System.out.println("YAML optimization successful.");
            return true;

        } catch (IOException e) {
            System.err.println("Optimization Error: Could not optimize YAML file " + inputFilePath + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Optimization Error: An unexpected error occurred during YAML optimization: " + e.getMessage());
            return false;
        }
    }

    /**
     * Optimizes a .properties configuration file.
     * Current optimization: Sorts keys alphabetically and removes comments/empty lines.
     *
     * @param inputFilePath  The path to the input .properties file.
     * @param outputFilePath The path where the optimized .properties file will be saved.
     * @return True if optimization is successful, false otherwise.
     */
    public boolean optimizePropertiesConfig(String inputFilePath, String outputFilePath) {
        System.out.println("\nOptimizing Properties file: " + inputFilePath + " -> " + outputFilePath);
        Properties props = new Properties();
        try (FileReader reader = new FileReader(inputFilePath)) {
            props.load(reader); // Load properties, comments are usually lost here

            // Create a TreeMap from properties to sort keys alphabetically
            TreeMap<String, String> sortedProps = new TreeMap<>();
            props.stringPropertyNames().forEach(key -> sortedProps.put(key, props.getProperty(key)));

            try (FileWriter writer = new FileWriter(outputFilePath)) {
                // Write sorted properties back to the file
                // Properties.store writes a timestamp and comments. We can write manually for more control.
                StringWriter stringWriter = new StringWriter();
                // Store without timestamp and comments by writing manually
                for (Map.Entry<String, String> entry : sortedProps.entrySet()) {
                    stringWriter.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
                }
                writer.write(stringWriter.toString());
            }

            System.out.println("Properties optimization successful.");
            return true;

        } catch (IOException e) {
            System.err.println("Optimization Error: Could not optimize Properties file " + inputFilePath + ": " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.err.println("Optimization Error: An unexpected error occurred during Properties optimization: " + e.getMessage());
            return false;
        }
    }

    /**
     * Loads a YAML configuration file into the AppConfig POJO.
     * This simulates how the application would consume the final, validated config.
     *
     * @param yamlFilePath The path to the YAML file.
     * @return An AppConfig object populated with data from the YAML.
     * @throws IOException If the file cannot be read or parsed.
     */
    public AppConfig loadAppConfigFromYaml(String yamlFilePath) throws IOException {
        try (FileReader reader = new FileReader(yamlFilePath)) {
            // Use a specific constructor for AppConfig class
            Yaml pojoYaml = new Yaml(new Constructor(AppConfig.class));
            return pojoYaml.load(reader);
        }
    }

    /**
     * Loads a .properties configuration file into a Properties object.
     * This simulates how the application would consume the final, validated config.
     *
     * @param propertiesFilePath The path to the .properties file.
     * @return A Properties object populated with data from the file.
     * @throws IOException If the file cannot be read or parsed.
     */
    public Properties loadPropertiesConfig(String propertiesFilePath) throws IOException {
        Properties props = new Properties();
        try (FileReader reader = new FileReader(propertiesFilePath)) {
            props.load(reader);
            return props;
        }
    }

    /**
     * Inner class to represent the structure of the mocked GenAI response JSON.
     * This helps in easily parsing the "proposed_yaml" and "proposed_properties" fields.
     */
    private static class JsonGenAIResponse {
        private String proposedYaml;
        private String proposedProperties;

        public String getProposedYaml() {
            return proposedYaml;
        }

        public void setProposedYaml(String proposedYaml) {
            this.proposedYaml = proposedYaml;
        }

        public String getProposedProperties() {
            return proposedProperties;
        }

        public void setProposedProperties(String proposedProperties) {
            this.proposedProperties = proposedProperties;
        }
    }
}