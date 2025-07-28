package com.genai.chatops.config;

import com.theokanning.openai.service.OpenAiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@Slf4j
public class OpenAIConfig {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    @Bean
    public OpenAiService openAiService() {
        log.info("Initializing OpenAI Service...");
        // Initialize OpenAiService with the API key and a timeout
        // The default timeout might be too short for some LLM responses.
        return new OpenAiService(openAiApiKey, Duration.ofSeconds(60)); // 60-second timeout
    }
}