# Multi-stage Dockerfile for a Java Maven application.
# GenAI can help in structuring multi-stage builds for efficiency,
# selecting appropriate base images, and optimizing layer caching.

# --- Build Stage ---
# Use an official Maven image with Java 11 for building the application.
# GenAI can suggest suitable base images and versions.
FROM maven:3.9.5-amazoncorretto-11-alpine AS build

# Set the working directory inside the container.
WORKDIR /app

# Copy the Maven pom.xml file first to leverage Docker's layer caching.
# If pom.xml doesn't change, Maven dependencies won't be re-downloaded.
COPY pom.xml .

# Download project dependencies. This step can be re-used by GenAI for faster iterations.
# GenAI can also suggest 'mvn dependency:go-offline' for disconnected builds.
RUN mvn dependency:go-offline

# Copy the source code.
COPY src ./src

# Package the application into an executable JAR.
# GenAI can suggest adding JaCoCo reports or other build artifacts.
RUN mvn clean install -DskipTests

# --- Runtime Stage ---
# Use a smaller, secure JRE-only base image for the final application.
# Alpine Linux is chosen for its small size. GenAI can suggest other distributions (e.g., Debian slim).
FROM public.ecr.aws/amazoncorretto/amazoncorretto:11-alpine-jre

# Set the working directory for the runtime environment.
WORKDIR /app

# Copy the built JAR from the build stage.
# GenAI can help extract specific files from multi-stage builds.
COPY --from=build /app/target/JavaGenAIDevOpsCompanion-1.0.0-SNAPSHOT.jar ./app.jar

# Expose the port if your application had a web server (e.g., Spring Boot).
# For this simple app, it's not strictly needed but good practice.
# EXPOSE 8080

# Define the command to run the application.
# GenAI can help with JVM arguments for memory tuning or performance.
ENTRYPOINT ["java", "-jar", "app.jar"]