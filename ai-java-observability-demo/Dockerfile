# Use a slim Java 17 base image
FROM eclipse-temurin:17-jre-focal

# Set the working directory inside the container
WORKDIR /app

# Copy the built Spring Boot fat JAR into the container
# The JAR file is typically located in the 'target' directory after 'mvn clean install'
# We rename it to 'app.jar' for simplicity
COPY target/ai-java-observability-demo-0.0.1-SNAPSHOT.jar app.jar

# Expose the port that the Spring Boot application listens on
EXPOSE 8080

# Define entrypoint to run the application.
# This entrypoint is specifically designed to allow easy injection of Java agents
# via environment variables.

# Using 'sh -c' allows for command substitution for JVM options.
# For example: JAVA_TOOL_OPTIONS="-javaagent:/agents/newrelic/newrelic.jar"
# This variable is often used by orchestrators (like Kubernetes) to inject agents.
ENTRYPOINT ["sh", "-c", "java $JAVA_TOOL_OPTIONS -jar app.jar"]

# Example Docker build command:
# docker build -t ai-java-observability-demo .

# Example Docker run command (without agent):
# docker run -p 8080:8080 ai-java-observability-demo

# Example Docker run command with Dynatrace agent (assuming agent is mounted):
# docker run -p 8080:8080 \
#   -v /path/to/your/oneagent.jar:/agents/dynatrace/oneagent.jar \
#   -e JAVA_TOOL_OPTIONS="-javaagent:/agents/dynatrace/oneagent.jar" \
#   -e DT_TENANT=YOUR_DT_TENANT_ID \
#   -e DT_TENANTTOKEN=YOUR_DT_TENANT_TOKEN \
#   -e DT_CONNECTION_POINT=YOUR_DT_ENVIRONMENT_URL \
#   ai-java-observability-demo

# Example Docker run command with New Relic agent (assuming agent and config are mounted):
# docker run -p 8080:8080 \
#   -v /path/to/your/newrelic.jar:/agents/newrelic/newrelic.jar \
#   -v /path/to/your/newrelic.yml:/agents/newrelic/newrelic.yml \
#   -e JAVA_TOOL_OPTIONS="-javaagent:/agents/newrelic/newrelic.jar" \
#   -e NEW_RELIC_LICENSE_KEY=YOUR_NEW_RELIC_LICENSE_KEY \
#   -e NEW_RELIC_APP_NAME=ai-java-observability-demo \
#   ai-java-observability-demo