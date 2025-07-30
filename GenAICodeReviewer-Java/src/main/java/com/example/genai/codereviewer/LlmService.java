package com.example.genai.codereviewer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

// Service class to interact with the Large Language Model API.
public class LlmService {
    // LLM API endpoint URL. This example uses OpenAI's chat completions.
    private static final String LLM_API_URL = "https://api.openai.com/v1/chat/completions";
    // The model to use. GPT-4 or similar is recommended for code review.
    private static final String LLM_MODEL = "gpt-4o"; // Or "gpt-3.5-turbo", "gpt-4" etc.
    private final String apiKey;
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;

    // Constructor initializes with the API key and sets up the HTTP client.
    public LlmService(String apiKey) {
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new IllegalArgumentException("LLM_API_KEY environment variable is not set.");
        }
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        // Configure OkHttpClient with a timeout to prevent hanging requests.
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    // Generates a prompt for the LLM based on the provided code diff.
    // This prompt instructs the LLM on its role, desired output format (JSON),
    // and specific areas of focus for the code review.
    private String generatePrompt(String codeDiff) {
        return "You are an expert Java code reviewer. Review the following code changes (diff format) and provide " +
               "constructive feedback. Focus on potential bugs, performance issues, security vulnerabilities, " +
               "best practices, and code clarity.\n" +
               "Respond with a JSON array where each object has 'path' (the file path relative to repository root), " +
               "'start_line' (the starting line number in the *new* file, 1-indexed), 'end_line' (optional, ending line number " +
               "in the new file, if 0 or omitted, it's a single line or file-level comment), and 'body' (the review comment text).\n" +
               "If no specific line is applicable or for a file-level comment, use 0 for 'start_line' and 'end_line'.\n" +
               "If no issues are found or suggestions can be made, return an empty array [] to indicate no comments.\n\n" +
               "Example JSON output for one comment:\n" +
               "[\n" +
               "  {\n" +
               "    \"path\": \"src/main/java/com/example/MyClass.java\",\n" +
               "    \"start_line\": 25,\n" +
               "    \"end_line\": 28,\n" +
               "    \"body\": \"Consider using try-with-resources for automatically closing the InputStream.\"\n" +
               "  }\n" +
               "]\n\n" +
               "Code Diff to review:\n" +
               "