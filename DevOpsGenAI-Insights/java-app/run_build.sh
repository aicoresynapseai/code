#!/bin/bash
set -e # Exit immediately if a command exits with a non-zero status

# This script simulates a CI/CD pipeline step:
# 1. Builds and runs tests for the Java application.
# 2. Determines the build status (SUCCESS/FAILURE).
# 3. Extracts simulated test failure messages if any.
# 4. Constructs a JSON payload.
# 5. Sends the JSON payload to the feedback-service API endpoint.

echo "--- Starting simulated Java App build and test execution ---"

# Generate a unique build ID
BUILD_ID=$(date +%s)
PROJECT_ID="java-web-app"
SERVICE_URL="http://localhost:8080/api/telemetry/build"

# Determine if we should simulate a failure based on an argument
SIMULATE_FAILURE_ARG="${1:-false}" # Default to false if no argument provided

echo "Simulating failure: $SIMULATE_FAILURE_ARG"

# Run Maven tests. We use a system property to control test outcome for the demo.
# Note: `mvn test` directly doesn't produce an exit code of 1 on test failures,
# but `mvn install` or `mvn verify` does. For this demo, we'll check `mvn test` output
# or simply use the system property to pre-determine failure.
# We'll rely on the `SIMULATE_FAILURE_ARG` to control the test run.
if [ "$SIMULATE_FAILURE_ARG" = "true" ]; then
    echo "Running Maven tests with simulated failures..."
    # Run Maven clean install with system property to trigger failures
    set +e # Temporarily disable exit on error to capture Maven's exit code
    mvn clean install -Dsimulate.failure=true -Dmaven.test.failure.ignore=false > maven_build_output.log 2>&1
    MAVEN_EXIT_CODE=$?
    set -e # Re-enable exit on error
else
    echo "Running Maven tests without simulated failures (expecting success)..."
    mvn clean install -Dsimulate.failure=false > maven_build_output.log 2>&1
    MAVEN_EXIT_CODE=$?
fi

BUILD_STATUS="FAILURE"
TOTAL_TESTS=5 # Based on MyApplicationTest.java
PASSED_TESTS=0
FAILED_TESTS=0
ERROR_MESSAGES_JSON="[]" # Default empty array

echo "Maven exit code: $MAVEN_EXIT_CODE"

if [ $MAVEN_EXIT_CODE -eq 0 ]; then
    BUILD_STATUS="SUCCESS"
    PASSED_TESTS=$TOTAL_TESTS
    echo "Build SUCCESS."
else
    BUILD_STATUS="FAILURE"
    # For a real scenario, you'd parse Surefire reports (target/surefire-reports/*.xml)
    # to get exact failure details. For this demo, we'll simulate them based on the
    # `SIMULATE_FAILURE_ARG`.
    echo "Build FAILURE. Simulating error messages..."
    FAILED_TESTS=$TOTAL_TESTS # Assume all fail if `simulate.failure` is true for simplicity
    PASSED_TESTS=0

    # Simulate specific error messages that the AI can act on
    if [ "$SIMULATE_FAILURE_ARG" = "true" ]; then
        ERROR_MESSAGES=(
            "Simulated NullPointerException: User service data could not be loaded."
            "Expected 12 database records but found 10. Database connectivity issue or data integrity problem."
            "ArithmeticException: Cannot divide by zero." # Even if caught, we can simulate an unhandled one
        )
        # Convert bash array to JSON array
        ERROR_MESSAGES_JSON=$(printf '%s\n' "${ERROR_MESSAGES[@]}" | jq -R . | jq -s .)
    fi
fi

# Simulate build time
BUILD_TIME_SECONDS=$(( (RANDOM % 60) + 30 )) # Between 30 and 90 seconds

# Construct JSON payload
JSON_PAYLOAD=$(jq -n \
    --arg projectId "$PROJECT_ID" \
    --arg buildId "$BUILD_ID" \
    --arg status "$BUILD_STATUS" \
    --argjson totalTests "$TOTAL_TESTS" \
    --argjson passedTests "$PASSED_TESTS" \
    --argjson failedTests "$FAILED_TESTS" \
    --argjson errorMessages "$ERROR_MESSAGES_JSON" \
    --argjson buildTimeSeconds "$BUILD_TIME_SECONDS" \
    '{
        projectId: $projectId,
        buildId: ($buildId | tonumber),
        status: $status,
        totalTests: ($totalTests | tonumber),
        passedTests: ($passedTests | tonumber),
        failedTests: ($failedTests | tonumber),
        errorMessages: $errorMessages,
        buildTimeSeconds: ($buildTimeSeconds | tonumber)
    }')

echo "--- Sending telemetry to Feedback Service ---"
echo "$JSON_PAYLOAD" | jq . # Pretty print JSON before sending

# Send the payload to the feedback service
curl -X POST -H "Content-Type: application/json" -d "$JSON_PAYLOAD" "$SERVICE_URL"
echo -e "\n--- Telemetry sent. ---"

# Clean up build output log
rm -f maven_build_output.log

echo "--- Simulated build and telemetry sending complete ---"