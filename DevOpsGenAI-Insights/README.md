DevOpsGenAI-Insights: Real-time DevOps Feedback Loops via AI for Java Teams

This project demonstrates how to set up a system that provides real-time, AI-powered feedback to development teams based on build and test telemetry. It simulates a Java development workflow, captures build/test results, sends them to a central feedback service, which then uses a simulated Generative AI (GenAI) model to analyze the data and provide actionable insights. These insights are displayed on a web dashboard and can be imagined as notifications to a bot.

Project Structure:
- `java-app/`: A sample Java Maven project with JUnit tests. This simulates a typical developer project in a CI/CD pipeline.
- `feedback-service/`: A Spring Boot application that acts as the central intelligence hub. It receives build telemetry, processes it, interacts with a simulated AI model for analysis, and serves a dashboard.
- `run_demo.sh`: An orchestration script to set up, run, and demonstrate the entire feedback loop.

How it Works:
1.  The `java-app` simulates a build and test run (using Maven and JUnit).
2.  After the build, `run_build.sh` within `java-app` collects simplified telemetry (build status, test counts, simulated error messages).
3.  This telemetry is then sent as a JSON payload to the `feedback-service`'s API endpoint.
4.  The `feedback-service` receives the telemetry and passes it to an `AIService` (which simulates a GenAI model).
5.  The `AIService` analyzes the telemetry (e.g., identifying common error patterns like NullPointerExceptions or AssertionErrors) and generates an analysis summary and suggested fixes. In a real-world scenario, this would involve calling a true GenAI API (like OpenAI's GPT, Google's Gemini, Anthropic's Claude, etc.).
6.  The `feedback-service` stores this information and makes it available via a web dashboard. It also prints the AI insights to the console, simulating a bot notification.
7.  Developers can then view the dashboard or receive bot notifications to get immediate, intelligent feedback on their build failures, helping them diagnose and fix issues faster.

Prerequisites:
- Java 17 or higher
- Maven 3.6.3 or higher
- `curl` (usually pre-installed on Linux/macOS)
- `jq` (a lightweight and flexible command-line JSON processor)

Setup and Running the Demo:

1.  Clone this repository:
    (Assume you have this code in a directory, e.g., `DevOpsGenAI-Insights`)

2.  Make the demo script executable:
    chmod +x run_demo.sh
    chmod +x java-app/run_build.sh

3.  Run the demo:
    ./run_demo.sh

    This script will:
    -   Start the `feedback-service` in the background.
    -   Wait for the `feedback-service` to be fully up.
    -   Trigger a simulated build in `java-app` that is configured to FAIL (to demonstrate AI analysis of failures).
    -   Send the failure telemetry to `feedback-service`.
    -   Trigger another simulated build in `java-app` that is configured to PASS (to demonstrate successful feedback).
    -   Send the success telemetry to `feedback-service`.
    -   Print instructions to access the dashboard.
    -   Wait for user input before cleaning up and stopping the `feedback-service`.

4.  Access the Dashboard:
    While the `feedback-service` is running, open your web browser and navigate to:
    http://localhost:8080/dashboard

    You will see the build reports and the AI-generated feedback. Also, observe the console output of the `feedback-service` for "bot" notifications.

5.  Experiment:
    -   Modify `java-app/src/test/java/com/devopsgenai/app/MyApplicationTest.java` to make different tests fail or pass.
    -   Run `java-app/run_build.sh` manually after modifications to send new telemetry.
    -   Observe how the AI feedback changes on the dashboard and in the console.

Cleanup:
The `run_demo.sh` script will prompt you to press ENTER to stop the `feedback-service` and clean up. You can also manually stop it by finding the process (e.g., `jps -l` or `lsof -i :8080`) and killing it.

This project serves as a conceptual demonstration. In a production environment, the AI integration would involve real API calls, a more robust data persistence layer (database), and integration with actual CI/CD tools (Jenkins, GitLab CI, GitHub Actions) and communication platforms (Slack, Microsoft Teams).