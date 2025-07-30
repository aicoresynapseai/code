#!/bin/bash

# Main script to demonstrate the Real-time DevOps Feedback Loops via AI for Java Teams.
# This script orchestrates the following:
# 1. Builds the Spring Boot feedback service.
# 2. Starts the feedback service in the background.
# 3. Triggers a Java app build that SIMULATES FAILURE and sends telemetry.
# 4. Triggers another Java app build that SIMULATES SUCCESS and sends telemetry.
# 5. Provides instructions to view the dashboard.
# 6. Cleans up by stopping the feedback service.

# --- Configuration ---
FEEDBACK_SERVICE_DIR="feedback-service"
JAVA_APP_DIR="java-app"
SERVICE_PORT=8080
SERVICE_URL="http://localhost:$SERVICE_PORT"

echo "--- Starting DevOpsGenAI-Insights Demo ---"

# --- 1. Build the Feedback Service ---
echo "Building the Feedback Service..."
(cd "$FEEDBACK_SERVICE_DIR" && mvn clean package -DskipTests)

if [ $? -ne 0 ]; then
    echo "ERROR: Failed to build feedback-service. Exiting."
    exit 1
fi
echo "Feedback Service built successfully."

# --- 2. Start the Feedback Service in the background ---
echo "Starting the Feedback Service in the background..."
java -jar "$FEEDBACK_SERVICE_DIR/target/feedback-service-0.0.1-SNAPSHOT.jar" > feedback-service.log 2>&1 &
SERVICE_PID=$!
echo "Feedback Service started with PID: $SERVICE_PID"

# --- Wait for the service to be up ---
echo "Waiting for Feedback Service to be fully up and running..."
for i in {1..20}; do
    curl -s "$SERVICE_URL/actuator/health" > /dev/null && break
    echo "Service not yet ready, waiting... ($i/20)"
    sleep 3
done

if ! curl -s "$SERVICE_URL/actuator/health" > /dev/null; then
    echo "ERROR: Feedback Service did not start in time. Check feedback-service.log for details."
    kill $SERVICE_PID
    exit 1
fi
echo "Feedback Service is up and running!"

# --- 3. Trigger Java App Build (Simulate Failure) ---
echo -e "\n--- Triggering Java App build to SIMULATE FAILURE and send telemetry ---"
(cd "$JAVA_APP_DIR" && ./run_build.sh true) # Pass 'true' to simulate failure

if [ $? -ne 0 ]; then
    echo "WARNING: Simulated failure build script had issues. Continue demo."
fi

# Give a moment for the service to process
sleep 2

# --- 4. Trigger Java App Build (Simulate Success) ---
echo -e "\n--- Triggering Java App build to SIMULATE SUCCESS and send telemetry ---"
(cd "$JAVA_APP_DIR" && ./run_build.sh false) # Pass 'false' to simulate success

if [ $? -ne 0 ]; then
    echo "WARNING: Simulated success build script had issues. Continue demo."
fi

# Give a moment for the service to process
sleep 2

# --- 5. Instructions to view the dashboard ---
echo -e "\n--- Demo Orchestration Complete ---"
echo "The feedback service is running and has processed two simulated builds."
echo "You can now view the AI-powered feedback dashboard:"
echo "Open your web browser and go to: $SERVICE_URL/dashboard"
echo "Also, observe the console where this script is running for 'bot' notifications from the feedback service."

echo -e "\nPress ENTER to stop the Feedback Service and clean up..."
read -r # Wait for user input

# --- 6. Clean up: Stop the Feedback Service ---
echo -e "\nStopping the Feedback Service (PID: $SERVICE_PID)..."
kill $SERVICE_PID
wait $SERVICE_PID 2>/dev/null # Wait for the process to actually terminate
echo "Feedback Service stopped."

# Clean up log files
rm -f feedback-service.log

echo "--- Demo Finished ---"