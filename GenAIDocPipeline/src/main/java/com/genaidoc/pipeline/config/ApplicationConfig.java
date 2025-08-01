package com.genaidoc.pipeline.config;

/**
 * Configuration class for the application.
 * This class holds various settings that might be loaded from environment variables,
 * configuration files, or other sources in a real-world scenario.
 */
public class ApplicationConfig {
    private final String serviceName;
    private final int connectionTimeoutMs;

    /**
     * Constructs an ApplicationConfig instance.
     * @param serviceName The name of the service this configuration applies to.
     * @param connectionTimeoutMs The timeout in milliseconds for connections.
     */
    public ApplicationConfig(String serviceName, int connectionTimeoutMs) {
        this.serviceName = serviceName;
        this.connectionTimeoutMs = connectionTimeoutMs;
    }

    /**
     * Gets the name of the service.
     * @return The service name.
     */
    public String getServiceName() {
        return serviceName;
    }

    /**
     * Gets the connection timeout in milliseconds.
     * @return The connection timeout.
     */
    public int getConnectionTimeoutMs() {
        return connectionTimeoutMs;
    }

    @Override
    public String toString() {
        return "ApplicationConfig{" +
               "serviceName='" + serviceName + '\'' +
               ", connectionTimeoutMs=" + connectionTimeoutMs +
               '}';
    }
}