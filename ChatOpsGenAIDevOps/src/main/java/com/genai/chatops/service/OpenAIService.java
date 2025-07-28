package com.genai.chatops.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class OpenAIService {

    private final OpenAiService openAiService;
    private final ObjectMapper objectMapper = new ObjectMapper(); // For JSON parsing

    @Value("${openai.model.name}")
    private String openAiModelName;

    /**
     * Sends a natural language query to the OpenAI LLM and attempts to get a structured JSON response
     * representing a command and its parameters.
     *
     * @param userQuery The natural language query from the user (e.g., "deploy app to staging latest").
     * @param botMentionId The Discord bot's user ID, used to remove mentions from the query for cleaner LLM input.
     * @return An Optional containing a JsonNode if a valid command structure is parsed, otherwise empty.
     */
    public Optional<JsonNode> getStructuredCommandFromQuery(String userQuery, long botMentionId) {
        // Clean the query by removing the bot's mention
        String cleanedQuery = userQuery.replace("<@" + botMentionId + ">", "").trim();
        log.info("Cleaned query for LLM: '{}'", cleanedQuery);

        // Define the expected structure for LLM output (JSON schema)
        String systemPrompt = "You are a ChatOps assistant for Java DevOps. Your primary task is to interpret " +
                "user requests related to application deployments, status checks, and rollbacks. " +
                "Respond ONLY with a JSON object. If you understand the command, use the following structure:\n" +
                "{\n" +
                "  \"command\": \"deploy\" | \"status\" | \"rollback\" | \"help\",\n" +
                "  \"environment\": \"dev\" | \"staging\" | \"production\" | null,\n" +
                "  \"version\": \"latest\" | \"<version_number>\" | null\n" +
                "}\n" +
                "For 'help' command, 'environment' and 'version' should be null. " +
                "If the request is ambiguous or cannot be mapped to a command, provide a 'suggestions' field " +
                "with an array of relevant commands. For example: `{\"suggestions\": [\"deploy <env> <version>\", \"status <env>\", \"rollback <env> <version>\"]}`. " +
                "Ensure version is extracted correctly if present (e.g., 'v1.0.0', '1.2.3'). Use 'latest' if no specific version is mentioned for deployment. " +
                "If no environment is mentioned for deployment, default to 'dev'. " +
                "Strictly adhere to the JSON format. Do not include any other text outside the JSON.";

        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage(ChatMessageRole.SYSTEM.value(), systemPrompt),
                new ChatMessage(ChatMessageRole.USER.value(), cleanedQuery)
        );

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(openAiModelName)
                .messages(messages)
                .maxTokens(200) // Limit response length
                .temperature(0.0) // Make the response deterministic for command parsing
                .build();

        try {
            log.debug("Sending chat completion request to OpenAI: {}", chatCompletionRequest);
            String llmResponseContent = openAiService.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent();
            log.info("LLM Raw Response: {}", llmResponseContent);

            // Attempt to parse the LLM's response as JSON
            JsonNode jsonResponse = objectMapper.readTree(llmResponseContent);
            return Optional.of(jsonResponse);

        } catch (JsonProcessingException e) {
            log.error("LLM response was not valid JSON: {}", e.getMessage());
            return Optional.empty();
        } catch (Exception e) {
            log.error("Error communicating with OpenAI API: {}", e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Generates a conversational response from the LLM based on a given prompt.
     * Used for general queries, success messages, error explanations, etc.
     *
     * @param prompt The user's prompt or a system-generated prompt for a conversational response.
     * @return The generated text response from the LLM.
     */
    public String getConversationalResponse(String prompt) {
        List<ChatMessage> messages = Arrays.asList(
                new ChatMessage(ChatMessageRole.SYSTEM.value(), "You are a helpful and friendly ChatOps assistant for Java DevOps. " +
                        "Provide concise and clear responses. Be encouraging for successful operations and " +
                        "provide clear guidance for failures or help requests."),
                new ChatMessage(ChatMessageRole.USER.value(), prompt)
        );

        ChatCompletionRequest chatCompletionRequest = ChatCompletionRequest.builder()
                .model(openAiModelName)
                .messages(messages)
                .maxTokens(250) // Adjust as needed
                .temperature(0.7) // Allow for more creative/human-like responses
                .build();

        try {
            String llmResponseContent = openAiService.createChatCompletion(chatCompletionRequest)
                    .getChoices().get(0).getMessage().getContent();
            log.debug("LLM Conversational Response: {}", llmResponseContent);
            return llmResponseContent;
        } catch (Exception e) {
            log.error("Error getting conversational response from OpenAI: {}", e.getMessage());
            return "Apologies, I'm having trouble connecting to my brain (OpenAI API) right now. Please try again later.";
        }
    }
}