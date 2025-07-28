package com.genai.chatops.config;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.presence.Presence;
import discord4j.gateway.intent.Intent;
import discord4j.gateway.intent.IntentSet;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Optional;

@Configuration
@Slf4j
public class DiscordConfig {

    @Value("${discord.bot.token}")
    private String discordBotToken;

    @Bean
    public GatewayDiscordClient gatewayDiscordClient() {
        log.info("Attempting to connect to Discord with token: {}", Optional.ofNullable(discordBotToken).map(t -> t.substring(0, 5) + "...").orElse("N/A"));

        // Build Discord client with specified token and intents
        // MESSAGE_CONTENT is crucial for the bot to read message content
        GatewayDiscordClient client = DiscordClientBuilder.create(discordBotToken)
                .build()
                .login()
                .doOnSuccess(gateway -> {
                    log.info("Discord Bot connected successfully! Logged in as: {}", gateway.getSelf().block().getUsername());
                    // Set bot's presence (optional)
                    gateway.updatePresence(Presence.online()).subscribe();
                })
                .doOnError(error -> log.error("Error connecting to Discord: {}", error.getMessage()))
                .block(); // Block until connection is established

        if (client == null) {
            throw new RuntimeException("Failed to connect to Discord Gateway.");
        }

        // Verify required intents are enabled. MESSAGE_CONTENT must be enabled in Discord Developer Portal too.
        IntentSet intents = client.getGatewayInfo().block().getIntents();
        if (!intents.contains(Intent.MESSAGE_CONTENT)) {
            log.warn("MESSAGE_CONTENT intent is not enabled. The bot might not be able to read message content correctly. " +
                    "Please enable it in Discord Developer Portal under your bot's settings.");
        }

        return client;
    }
}