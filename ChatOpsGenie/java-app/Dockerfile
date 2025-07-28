# Use a slim OpenJDK image as the base for building the Java application
FROM eclipse-temurin:17-jdk-focal as build

# Set the working directory inside the container
WORKDIR /app

# Copy the Maven project files to the container
COPY pom.xml .
COPY src ./src

# Build the Spring Boot application using Maven.
# The 'clean package' command compiles the code, runs tests, and packages it into a JAR.
RUN mvn clean package -DskipTests

# Use a smaller JRE image for the final running application to reduce image size
FROM eclipse-temurin:17-jre-focal

# Set the working directory
WORKDIR /app

# Copy the built JAR file from the 'build' stage to the final image
COPY --from=build /app/target/*.jar app.jar

# Expose the port that the Spring Boot application will run on
EXPOSE 8080

# Define the command to run the application when the container starts
ENTRYPOINT ["java", "-jar", "app.jar"]