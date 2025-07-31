#!/bin/bash
# Script to run the Spring Boot application with the Dynatrace Java Agent attached.

# IMPORTANT: Before running, ensure you have downloaded the Dynatrace OneAgent JAR
# and placed it in the 'agents/dynatrace/' directory.
# Replace the placeholder values with your actual Dynatrace environment details.

# Path to the Dynatrace OneAgent JAR file
DT_AGENT_PATH="agents/dynatrace/oneagent.jar"

# --- Dynatrace Connection Details ---
# These variables configure the agent to connect to your Dynatrace environment.
# Obtain these from your Dynatrace SaaS/Managed environment (e.g., in "Deploy Dynatrace > Install OneAgent > Java").
# The exact variables might vary slightly based on your Dynatrace version and setup.
# Typical variables include:
# DT_TENANT: Your Dynatrace environment ID (e.g., abc12345.live.dynatrace.com)
# DT_TENANTTOKEN: The environment token (often for authentication)
# DT_CONNECTION_POINT: The endpoint(s) for the agent to send data to (e.g., https://your_dt_cluster/communication)
# DT_CLUSTER_ID: (Older setups) or similar for multi-tenant environments.

# Example placeholders - YOU MUST REPLACE THESE
export DT_TENANT="YOUR_DYNATRACE_TENANT_ID"             # e.g., 'abc12345.live.dynatrace.com'
export DT_TENANTTOKEN="YOUR_DYNATRACE_TENANT_TOKEN"     # e.g., 'your_secure_token'
export DT_CONNECTION_POINT="YOUR_DYNATRACE_CONNECTION_POINT" # e.g., 'https://your_dt_cluster_url/communication'
export DT_CLUSTER_ID="YOUR_CLUSTER_ID"                  # Only if explicitly required by your Dynatrace setup

echo "Starting Spring Boot application with Dynatrace OneAgent..."
echo "DT_TENANT: $DT_TENANT"
echo "DT_CONNECTION_POINT: $DT_CONNECTION_POINT"

# Check if the agent JAR exists
if [ ! -f "$DT_AGENT_PATH" ]; then
    echo "Error: Dynatrace OneAgent JAR not found at $DT_AGENT_PATH"
    echo "Please download 'oneagent.jar' from your Dynatrace environment and place it in the 'agents/dynatrace/' directory."
    exit 1
fi

# Run the Spring Boot JAR with the Dynatrace agent attached
# The -javaagent argument tells the JVM to load the agent at startup.
# Other DT_* environment variables are automatically picked up by the agent.
java -javaagent:$DT_AGENT_PATH \
     -jar target/ai-java-observability-demo-0.0.1-SNAPSHOT.jar

echo "Dynatrace agent started. Check your Dynatrace environment for data."