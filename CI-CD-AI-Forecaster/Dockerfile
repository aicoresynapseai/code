# Use a slimmed-down OpenJDK base image for smaller Docker images
FROM openjdk:17-jdk-slim

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file and its dependencies from the Maven target directory
# Assuming the JAR is named ci-cd-ai-forecaster-1.0.0-SNAPSHOT.jar and libs are in target/lib
COPY target/ci-cd-ai-forecaster-1.0.0-SNAPSHOT.jar ci-cd-ai-forecaster.jar
COPY target/lib/* lib/

# Copy the data directory containing historical CSVs
COPY target/data data/

# Copy the application properties (resources)
COPY src/main/resources/application.properties ./src/main/resources/application.properties

# Command to run the application when the container starts
# The -Dloader.path is crucial for Spring Boot (or similar classpath loaders) 
# to find dependencies in the 'lib' directory
ENTRYPOINT ["java", "-Dloader.path=lib/", "-jar", "ci-cd-ai-forecaster.jar"]

# Expose any ports if your application were a web service (not applicable for this console app)
# EXPOSE 8080