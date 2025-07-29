#!/bin/bash

# Exit immediately if a command exits with a non-zero status.
set -e

PROJECT_NAME="ai-optidock-java"
APP_VERSION="1.0.0" # Example version for the Docker image
DOCKER_IMAGE_NAME="${PROJECT_NAME}:${APP_VERSION}"
DOCKERFILE_PATH="Dockerfile.recommended"

echo "--- Building Java Microservice ---"
# Compile the Java application and package it into a JAR.
# 'mvn clean package' cleans the target directory and then compiles and packages the project.
# '-DskipTests' is often used in CI/CD to speed up builds if tests are run in a separate stage.
mvn clean package -DskipTests

echo ""
echo "--- Simulating AI-Driven Dockerfile Recommendation ---"
# This step simulates a GenAI tool recommending an optimized Dockerfile.
# The 'recommend_dockerfile.py' script will generate or update 'Dockerfile.recommended'.
python3 recommend_dockerfile.py

echo ""
echo "--- Building Docker Image with AI-Recommended Dockerfile ---"
# Build the Docker image using the generated/recommended Dockerfile.
# '-t' tags the image with a name and version.
# '-f' specifies the Dockerfile to use.
# '.' indicates the build context (current directory), where Maven target and Dockerfile reside.
docker build -t "${DOCKER_IMAGE_NAME}" -f "${DOCKERFILE_PATH}" .

echo ""
echo "--- Docker Image Build Complete ---"
echo "Image created: ${DOCKER_IMAGE_NAME}"
echo "You can now run it: docker run -p 8080:8080 ${DOCKER_IMAGE_NAME}"
echo "Or push to a registry: docker push ${DOCKER_IMAGE_NAME}"