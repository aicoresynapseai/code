package com.example.genai.codereviewer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

// Main application class for the GenAI Code Reviewer.
public class App {
    public static void main(String[] args) {
        // Retrieve the LLM API key from environment variables.
        // This is typically set as a GitHub Secret in GitHub Actions.
        String llmApiKey = System.getenv("LLM_API_KEY");
        if (llmApiKey == null || llmApiKey.isEmpty()) {
            System.err.println("Error: LLM_API_KEY environment variable is not set.");
            System.exit(1);
        }

        // The first argument is expected to be the path to the file containing the PR diff.
        if (args.length < 1) {
            System.err.println("Usage: java -jar genai-code-reviewer.jar <path_to_diff_file>");
            System.exit(1);
        }

        String diffFilePath = args[0];
        String codeDiff;
        try {
            // Read the entire diff content from the specified file.
            codeDiff = new String(Files.readAllBytes(Paths.get(diffFilePath)));
        } catch (IOException e) {
            System.err.println("Error reading diff file: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
            return; // Exit
        }

        // Initialize the LLM service with the API key.
        LlmService llmService = new LlmService(llmApiKey);
        List<ReviewComment> comments = Collections.emptyList();

        try {
            // Get review comments from the LLM based on the code diff.
            System.out.println("Sending code diff to LLM for review...");
            comments = llmService.getReviewComments(codeDiff);
            System.out.println("Received " + comments.size() + " comments from LLM.");
        } catch (Exception e) {
            System.err.println("An unexpected error occurred during LLM service call: " + e.getMessage());
            e.printStackTrace();
            // Continue to output empty or partial comments if an error occurs.
        }

        // Use ObjectMapper to serialize the list of comments into a JSON array.
        // This JSON output will be captured by the GitHub Action and used to post comments.
        ObjectMapper objectMapper = new ObjectMapper();
        // Enable pretty printing for better readability in logs (optional, disable for production).
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        try {
            // Print the JSON array of comments to standard output.
            // GitHub Actions will capture this output.
            System.out.println(objectMapper.writeValueAsString(comments));
        } catch (IOException e) {
            System.err.println("Error serializing comments to JSON: " + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }

        System.out.println("Code review process completed.");
    }
}