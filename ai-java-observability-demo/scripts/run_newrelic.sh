#!/bin/bash
# Script to run the Spring Boot application with the New Relic Java Agent attached.

# IMPORTANT: Before running, ensure you have downloaded 'newrelic.jar' and 'newrelic.yml'
# from your New Relic account and placed them in the 'agents/newrelic/' directory.
# You MUST configure your license key and app name in 'agents/newrelic/newrelic.yml'
# or via the environment variables below.

# Path to the New Relic Java Agent JAR file
NR_AGENT_PATH="agents/newrelic/newrelic.jar"

# Path to the New Relic agent configuration directory
# The newrelic.yml file should be in this directory.
NR_AGENT_CONFIG_DIR="agents/newrelic"

# --- New Relic Connection Details ---
# You can set these via environment variables or directly in newrelic.yml.
# Environment variables will override settings in newrelic.yml.

# New Relic License Key (required)
# Obtain this from your New Relic account (Account settings -> API Keys).
export NEW_RELIC_LICENSE_KEY="YOUR_NEW_RELIC_LICENSE_KEY" # REPLACE THIS

# Application Name (recommended for better organization in New Relic UI)
export NEW_RELIC_APP_NAME="ai-java-observability-demo" # Can be changed

echo "Starting Spring Boot application with New Relic Java Agent..."
echo "New Relic App Name: $NEW_RELIC_APP_NAME"

# Check if the agent JAR and config file exist
if [ ! -f "$NR_AGENT_PATH" ]; then
    echo "Error: New Relic Agent JAR not found at $NR_AGENT_PATH"
    echo "Please download 'newrelic.jar' from New Relic and place it in the 'agents/newrelic/' directory."
    exit 1
fi

if [ ! -f "$NR_AGENT_CONFIG_DIR/newrelic.yml" ]; then
    echo "Error: New Relic configuration file newrelic.yml not found at $NR_AGENT_CONFIG_DIR"
    echo "Please download 'newrelic.yml' from New Relic and place it in the 'agents/newrelic/' directory."
    exit 1
fi

# Validate license key is set
if [ "$NEW_RELIC_LICENSE_KEY" = "YOUR_NEW_RELIC_LICENSE_KEY" ] || [ -z "$NEW_RELIC_LICENSE_KEY" ]; then
    echo "Warning: NEW_RELIC_LICENSE_KEY is not set or is still the placeholder. New Relic will not report data."
    echo "Please set a valid license key in this script or in 'agents/newrelic/newrelic.yml'."
fi

# Run the Spring Boot JAR with the New Relic agent attached
# The -javaagent argument loads the agent.
# The -Dnewrelic.config.file specifies the path to newrelic.yml.
java -javaagent:$NR_AGENT_PATH \
     -Dnewrelic.config.file=$NR_AGENT_CONFIG_DIR/newrelic.yml \
     -jar target/ai-java-observability-demo-0.0.1-SNAPSHOT.jar

echo "New Relic agent started. Check your New Relic dashboard for data."