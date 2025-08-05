package com.genai.devops.tutor.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service layer to simulate GenAI interactions for tutoring Java DevOps.
 * In a real-world application, this service would integrate with actual
 * GenAI APIs (e.g., OpenAI, Google Gemini).
 */
@Service // Marks this class as a Spring Service component.
public class GenAITutorService {

    @Autowired // Injects Spring's ResourceLoader to access files from classpath.
    private ResourceLoader resourceLoader;

    private List<Map<String, String>> preDefinedFaqs; // Stores FAQs loaded from JSON.

    /**
     * Initializes the service by loading FAQs from a JSON file.
     * This method is called automatically after dependencies are injected.
     */
    @PostConstruct
    public void init() {
        // Load FAQs from the 'faqs.json' file located in the 'resources' directory.
        // This simulates a knowledge base that GenAI might query or augment.
        try {
            Resource resource = resourceLoader.getResource("classpath:faqs.json");
            try (InputStream is = resource.getInputStream()) {
                ObjectMapper mapper = new ObjectMapper();
                // Reads the JSON file and maps it to a List of Maps.
                preDefinedFaqs = mapper.readValue(is, new TypeReference<List<Map<String, String>>>() {});
            }
        } catch (IOException e) {
            // Log the error if the FAQs file cannot be loaded.
            System.err.println("Error loading FAQs: " + e.getMessage());
            preDefinedFaqs = new ArrayList<>(); // Initialize as empty list to prevent NullPointerException.
        }
    }

    /**
     * Simulates a GenAI chat response based on the user's query.
     * This is a simple keyword-based simulation.
     * In a real scenario, this would involve calling a GenAI model API
     * with an appropriate prompt.
     *
     * @param query The user's input query.
     * @return A simulated AI-generated response.
     */
    public String generateChatResponse(String query) {
        String lowerQuery = query.toLowerCase();

        if (lowerQuery.contains("ci/cd") || lowerQuery.contains("cicd")) {
            return "CI/CD stands for Continuous Integration/Continuous Delivery (or Deployment). It's a method to deliver apps frequently to customers by introducing automation into the stages of app development. CI merges code changes frequently, and CD automates the delivery of that code to production.";
        } else if (lowerQuery.contains("devops")) {
            return "DevOps is a set of practices that combines software development (Dev) and IT operations (Ops) to shorten the systems development life cycle and provide continuous delivery with high software quality. It aims to unify development and operations through collaboration and automation.";
        } else if (lowerQuery.contains("java version") || lowerQuery.contains("jdk")) {
            return "The latest Long-Term Support (LTS) version of Java is typically recommended for stable production environments, currently Java 17 or Java 21. For new projects, it's good to consider the newest LTS for its features and performance improvements.";
        } else if (lowerQuery.contains("maven") && lowerQuery.contains("dependency")) {
            return "In Maven, dependencies are declared in the <dependencies> section of your pom.xml. They tell Maven which libraries your project needs to compile and run. Maven automatically downloads and manages these transitively.";
        } else if (lowerQuery.contains("docker")) {
            return "Docker is a platform that uses OS-level virtualization to deliver software in packages called containers. Containers are isolated from one another and bundle their own software, libraries and configuration files; they can communicate with each other through well-defined channels.";
        } else if (lowerQuery.contains("kubernetes")) {
            return "Kubernetes is an open-source container-orchestration system for automating deployment, scaling, and management of containerized applications. It groups containers that make up an application into logical units for easy management and discovery.";
        } else if (lowerQuery.contains("cloud") || lowerQuery.contains("aws") || lowerQuery.contains("azure") || lowerQuery.contains("gcp")) {
            return "Cloud platforms like AWS, Azure, and GCP provide scalable infrastructure and services that are crucial for modern DevOps. They allow for easy provisioning of resources, serverless computing, and managed services for databases, messaging, and more.";
        } else if (lowerQuery.contains("unit test")) {
            return "Unit testing is a software testing method where individual units or components of a software are tested. In Java, frameworks like JUnit or TestNG are commonly used to write and execute unit tests, ensuring individual code pieces work as expected.";
        } else if (lowerQuery.contains("thank you") || lowerQuery.contains("thanks")) {
            return "You're welcome! I'm here to help you learn about Java DevOps. Feel free to ask more questions.";
        }
        return "I'm still learning, but I can help with topics like CI/CD, DevOps, Java, Maven, Docker, and Kubernetes. What specifically would you like to know?";
    }

    /**
     * Simulates GenAI explaining a given code snippet.
     * The explanation is a generic placeholder in this simulation.
     * A real GenAI would parse the code and provide detailed, context-aware explanations.
     *
     * @param codeSnippet The code string to explain.
     * @return A simulated AI-generated explanation of the code.
     */
    public String explainCodeSnippet(String codeSnippet) {
        // In a real scenario, this would send the codeSnippet to a GenAI model
        // with a prompt like "Explain the following Java code snippet:"
        if (codeSnippet.toLowerCase().contains("public static void main")) {
            return "This looks like a standard Java main method, the entry point for execution. It's declaring a class with a main method that prints 'Hello' to the console. Good start to Java programming!";
        } else if (codeSnippet.toLowerCase().contains("spring") && codeSnippet.toLowerCase().contains("controller")) {
            return "This snippet likely shows a Spring Boot REST Controller. The `@RestController` annotation combines `@Controller` and `@ResponseBody`, indicating that the class handles web requests and directly returns data. `@RequestMapping` defines the base path for its endpoints. It's fundamental for building RESTful APIs in Spring.";
        } else if (codeSnippet.toLowerCase().contains("dockerfile")) {
            return "This appears to be a Dockerfile. It specifies instructions for building a Docker image, typically including a base image (FROM), copying application files (COPY), and defining the command to run when the container starts (CMD/ENTRYPOINT). It's crucial for containerizing Java applications.";
        }
        return "This is a Java code snippet. A GenAI would analyze its syntax, identify patterns (e.g., loops, conditionals, class definitions), and explain its purpose, function, and potential best practices. For example, it could highlight object-oriented principles, data structures used, or potential performance considerations.";
    }

    /**
     * Simulates GenAI generating an onboarding guide for a specific topic.
     * The guide is a generic placeholder in this simulation.
     * A real GenAI would generate a structured, multi-step guide.
     *
     * @param topic The topic for which to generate the onboarding guide.
     * @return A simulated AI-generated onboarding guide.
     */
    public String generateOnboardingGuide(String topic) {
        // In a real scenario, this would prompt a GenAI model with:
        // "Generate a step-by-step onboarding guide for Java developers learning <topic>."
        String lowerTopic = topic.toLowerCase();
        if (lowerTopic.contains("git")) {
            return "Onboarding Guide: Git for Java Developers\n\n" +
                    "1. Install Git: Download and install Git on your system.\n" +
                    "2. Configure Git: Set your user name and email globally.\n" +
                    "3. Basic Commands: Learn `git clone`, `git add`, `git commit`, `git push`, `git pull`.\n" +
                    "4. Branching Strategy: Understand `git branch`, `git checkout`, `git merge` for feature development.\n" +
                    "5. IDE Integration: Learn to use Git from your IDE (IntelliJ, Eclipse, VS Code).\n" +
                    "6. .gitignore: Understand how to exclude files from version control.\n" +
                    "Git is essential for collaborative Java development.";
        } else if (lowerTopic.contains("maven")) {
            return "Onboarding Guide: Maven for Java Developers\n\n" +
                    "1. Install Maven: Ensure Maven is installed and configured in your PATH.\n" +
                    "2. pom.xml basics: Understand the Project Object Model (POM) file structure (groupId, artifactId, version).\n" +
                    "3. Dependencies: How to declare and manage project dependencies.\n" +
                    "4. Build Lifecycle: Learn phases like `compile`, `test`, `package`, `install`, `deploy`.\n" +
                    "5. Plugins: Understand how plugins extend Maven's capabilities.\n" +
                    "6. Profiles: How to manage different build environments.\n" +
                    "Maven is crucial for build automation and dependency management in Java.";
        } else if (lowerTopic.contains("docker")) {
            return "Onboarding Guide: Docker Basics for Java Developers\n\n" +
                    "1. Install Docker: Get Docker Desktop for your OS.\n" +
                    "2. Core Concepts: Understand Images, Containers, Registries, Volumes, Networks.\n" +
                    "3. Basic Commands: `docker pull`, `docker run`, `docker ps`, `docker stop`, `docker rm`.\n" +
                    "4. Dockerizing a Java App: Write a basic `Dockerfile` for a Spring Boot application.\n" +
                    "5. Building and Running: `docker build`, `docker run` for your Java app.\n" +
                    "6. Docker Compose: For multi-service applications (e.g., Java app + database).";
        }
        return "Here is a simulated onboarding guide for '" + topic + "':\n\n" +
               "1. **Introduction to " + topic + ":** Understand the core concepts and why it's important for DevOps.\n" +
               "2. **Key Tools and Technologies:** Identify essential tools associated with " + topic + ".\n" +
               "3. **Setup and Installation:** Step-by-step instructions to get started with basic setup.\n" +
               "4. **First Hands-On Exercise:** A simple project or task to apply initial knowledge.\n" +
               "5. **Best Practices and Common Pitfalls:** Tips for efficient usage and things to avoid.\n" +
               "6. **Further Learning Resources:** Links to documentation, tutorials, and communities.\n\n" +
               "This guide is a starting point, and a real GenAI could elaborate with specific commands and examples.";
    }

    /**
     * Retrieves all pre-defined FAQs.
     *
     * @return A list of maps, where each map contains a "question" and an "answer".
     */
    public List<Map<String, String>> getFAQs() {
        return new ArrayList<>(preDefinedFaqs); // Return a copy to prevent external modification.
    }

    /**
     * Simulates answering an FAQ question.
     * Tries to find a direct match in pre-defined FAQs first.
     * If no direct match, it uses a simplified GenAI-like approach based on keywords.
     *
     * @param question The user's question.
     * @return A simulated answer.
     */
    public String answerFAQQuestion(String question) {
        String lowerQuestion = question.toLowerCase();

        // 1. Try to find a direct match in pre-defined FAQs
        Optional<Map<String, String>> directMatch = preDefinedFaqs.stream()
                .filter(faq -> faq.get("question").toLowerCase().contains(lowerQuestion) ||
                               lowerQuestion.contains(faq.get("question").toLowerCase()))
                .findFirst();

        if (directMatch.isPresent()) {
            return directMatch.get().get("answer") + " (Found in FAQ database)";
        }

        // 2. If no direct match, use the general chat response logic as a fallback/AI enhancement
        // This simulates the GenAI generating an answer even if it's not a pre-defined FAQ.
        return generateChatResponse(question) + " (AI-generated or fallback)";
    }
}