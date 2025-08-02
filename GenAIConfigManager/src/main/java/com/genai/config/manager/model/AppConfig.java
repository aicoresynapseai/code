package com.genai.config.manager.model;

import java.util.Map;

/**
 * A simple POJO to represent the structure of our application's configuration.
 * This class is used to map parsed YAML or properties data into a strongly-typed object.
 */
public class AppConfig {
    private String appName;
    private String environment;
    private DatabaseConfig database;
    private Map<String, String> loggingLevels;
    private Map<String, Boolean> featureToggles;

    // Default constructor for deserialization
    public AppConfig() {
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public DatabaseConfig getDatabase() {
        return database;
    }

    public void setDatabase(DatabaseConfig database) {
        this.database = database;
    }

    public Map<String, String> getLoggingLevels() {
        return loggingLevels;
    }

    public void setLoggingLevels(Map<String, String> loggingLevels) {
        this.loggingLevels = loggingLevels;
    }

    public Map<String, Boolean> getFeatureToggles() {
        return featureToggles;
    }

    public void setFeatureToggles(Map<String, Boolean> featureToggles) {
        this.featureToggles = featureToggles;
    }

    @Override
    public String toString() {
        return "AppConfig{" +
                "appName='" + appName + '\'' +
                ", environment='" + environment + '\'' +
                ", database=" + database +
                ", loggingLevels=" + loggingLevels +
                ", featureToggles=" + featureToggles +
                '}';
    }

    /**
     * Nested class for database configuration details.
     */
    public static class DatabaseConfig {
        private String url;
        private String username;
        private String password;
        private String driver;

        public DatabaseConfig() {
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        @Override
        public String toString() {
            return "DatabaseConfig{" +
                    "url='" + url + '\'' +
                    ", username='" + username + '\'' +
                    ", password='[PROTECTED]'" + // Mask password for display
                    ", driver='" + driver + '\'' +
                    '}';
        }
    }
}