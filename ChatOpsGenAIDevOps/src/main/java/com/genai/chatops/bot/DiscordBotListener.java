package com.genai.chatops.bot;

import com.fasterxml.jackson.databind.JsonNode;
import com.genai.chatops.service.DeploymentService;
import com.genai.chatops.service.OpenAIService;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.discordjson.json.gateway.ReadyDispatch;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;

@Component
@RequiredArgsConstructor
@Slf4j
public class DiscordBotListener {

    private final OpenAIService openAIService;
    private final DeploymentService deploymentService;
    private final GatewayDiscordClient client;

    private Long botId; // To store the bot's own user ID for mentions

    @EventListener
    public void onReady(ReadyEvent event) {
        User self = event.getSelf();
        this.botId = self.getId().asLong(); // Store bot's ID
        log.info("Logged in as {}#{}", self.getUsername(), self.getDiscriminator());
        log.info("Bot is ready. My ID is: {}", botId);
    }

    @EventListener
    public Mono<Void> onMessage(MessageCreateEvent event) {
        Message message = event.getMessage();
        String content = message.getContent();

        // Check if the message is from a bot or empty
        if (message.getAuthor().map(User::isBot).orElse(true) || content.isBlank()) {
            return Mono.empty();
        }

        // Check if the bot is mentioned or if it's a direct message to the bot
        boolean isMentioned = message.getUserMentions().any(user -> user.getId().asLong() == botId).blockOptional().orElse(false);
        boolean isDirectMessage = message.getChannel().block() instanceof discord4j.core.object.entity.channel.PrivateChannel;

        if (!isMentioned && !isDirectMessage) {
            return Mono.empty(); // Ignore messages not directed at the bot
        }

        log.info("Received message: '{}' from user: {}", content, message.getAuthor().get().getUsername());

        // Get the channel to send responses
        MessageChannel channel = message.getChannel().block();
        if (channel == null) {
            log.warn("Could not retrieve channel for message.");
            return Mono.empty();
        }

        // Use the LLM to interpret the command
        return Mono.defer(() -> {
            channel.type().subscribe(); // Show typing indicator

            return openAIService.getStructuredCommandFromQuery(content, botId)
                    .map(jsonNode -> {
                        // Check for 'suggestions' field first, indicating LLM couldn't parse a command
                        if (jsonNode.has("suggestions") && jsonNode.get("suggestions").isArray()) {
                            StringBuilder suggestions = new StringBuilder("I couldn't fully understand that. Perhaps you meant one of these?\n");
                            jsonNode.get("suggestions").forEach(s -> suggestions.append("- `").append(s.asText()).append("`\n"));
                            return channel.createMessage(suggestions.toString());
                        }

                        // Parse command and parameters from the LLM's JSON response
                        String command = jsonNode.has("command") ? jsonNode.get("command").asText() : null;
                        String environment = jsonNode.has("environment") && !jsonNode.get("environment").isNull() ? jsonNode.get("environment").asText() : null;
                        String version = jsonNode.has("version") && !jsonNode.get("version").isNull() ? jsonNode.get("version").asText() : null;

                        log.info("Parsed command: {}, env: {}, ver: {}", command, environment, version);

                        switch (command) {
                            case "deploy":
                                if (environment == null) environment = "dev"; // Default to dev if not specified
                                if (version == null) version = "latest"; // Default to latest if not specified

                                String finalEnvironment = environment;
                                String finalVersion = version;

                                // Send initial message to Discord
                                return channel.createMessage(
                                        openAIService.getConversationalResponse(
                                                String.format("You asked me to deploy the application. " +
                                                        "Confirming deployment request for environment `%s` with version `%s`. " +
                                                        "Initiating deployment process...", finalEnvironment, finalVersion)
                                        )
                                ).then(Mono.defer(() -> {
                                    // Asynchronously run the deployment script
                                    CompletableFuture<Boolean> deploymentFuture = deploymentService.deployApplication(
                                            finalEnvironment, finalVersion,
                                            // Consumer for real-time script output
                                            scriptOutput -> channel.createMessage("