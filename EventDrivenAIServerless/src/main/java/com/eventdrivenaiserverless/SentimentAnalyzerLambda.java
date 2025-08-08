package com.eventdrivenaiserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.comprehend.ComprehendClient;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentRequest;
import software.amazon.awssdk.services.comprehend.model.DetectSentimentResponse;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * AWS Lambda handler for processing SQS events to perform sentiment analysis
 * using AWS Comprehend and store results in DynamoDB.
 */
public class SentimentAnalyzerLambda implements RequestHandler<SQSEvent, Void> {

    private static final Logger logger = LoggerFactory.getLogger(SentimentAnalyzerLambda.class);
    private final ComprehendClient comprehendClient;
    private final DynamoDbClient dynamoDbClient;
    private final ObjectMapper objectMapper; // For parsing JSON messages from SQS
    private final String tableName; // DynamoDB table name

    /**
     * Constructor for the Lambda handler. Initializes AWS SDK clients.
     * Clients are reused across invocations for better performance (warm start).
     */
    public SentimentAnalyzerLambda() {
        this.comprehendClient = ComprehendClient.builder()
                .region(Region.of(System.getenv("AWS_REGION"))) // Use the region where Lambda is deployed
                .build();
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.of(System.getenv("AWS_REGION"))) // Use the region where Lambda is deployed
                .build();
        this.objectMapper = new ObjectMapper();
        // Get the DynamoDB table name from environment variables, defined in template.yaml
        this.tableName = System.getenv("DYNAMODB_TABLE_NAME");
        if (this.tableName == null || this.tableName.isEmpty()) {
            logger.error("DYNAMODB_TABLE_NAME environment variable is not set.");
            throw new IllegalStateException("DYNAMODB_TABLE_NAME environment variable is required.");
        }
    }

    /**
     * Handles incoming SQS events.
     * Each SQS event can contain one or more records (messages).
     *
     * @param event The SQS event object containing message records.
     * @param context The Lambda execution context.
     * @return Void as this Lambda doesn't return a direct response to the caller.
     */
    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        logger.info("Received SQS event with {} records.", event.getRecords().size());

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                // The SQS message body is expected to be a JSON string like:
                // { "text": "This is a sample text for sentiment analysis." }
                String messageBody = message.getBody();
                logger.info("Processing SQS message: {}", messageBody);

                // Parse the JSON message to extract the 'text' field
                Map<String, String> messageMap = objectMapper.readValue(messageBody, Map.class);
                String textToAnalyze = messageMap.get("text");

                if (textToAnalyze == null || textToAnalyze.trim().isEmpty()) {
                    logger.warn("Skipping message with empty or missing 'text' field: {}", messageBody);
                    continue;
                }

                // 1. Perform Sentiment Analysis using AWS Comprehend
                DetectSentimentRequest sentimentRequest = DetectSentimentRequest.builder()
                        .text(textToAnalyze)
                        .languageCode("en") // Specify the language code (e.g., "en" for English)
                        .build();

                DetectSentimentResponse sentimentResponse = comprehendClient.detectSentiment(sentimentRequest);

                String sentiment = sentimentResponse.sentimentAsString(); // e.g., "POSITIVE", "NEGATIVE"
                float positiveScore = sentimentResponse.sentimentScore().positive();
                float negativeScore = sentimentResponse.sentimentScore().negative();
                float neutralScore = sentimentResponse.sentimentScore().neutral();
                float mixedScore = sentimentResponse.sentimentScore().mixed();

                logger.info("Text: '{}' -> Sentiment: {}", textToAnalyze, sentiment);

                // 2. Store Results in DynamoDB
                Map<String, AttributeValue> item = new HashMap<>();
                item.put("id", AttributeValue.builder().s(UUID.randomUUID().toString()).build()); // Unique ID for each record
                item.put("timestamp", AttributeValue.builder().s(Instant.now().toString()).build()); // Timestamp of analysis
                item.put("originalText", AttributeValue.builder().s(textToAnalyze).build());
                item.put("detectedSentiment", AttributeValue.builder().s(sentiment).build());
                item.put("positiveScore", AttributeValue.builder().n(String.valueOf(positiveScore)).build());
                item.put("negativeScore", AttributeValue.builder().n(String.valueOf(negativeScore)).build());
                item.put("neutralScore", AttributeValue.builder().n(String.valueOf(neutralScore)).build());
                item.put("mixedScore", AttributeValue.builder().n(String.valueOf(mixedScore)).build());

                PutItemRequest putItemRequest = PutItemRequest.builder()
                        .tableName(tableName)
                        .item(item)
                        .build();

                dynamoDbClient.putItem(putItemRequest);
                logger.info("Successfully stored sentiment analysis result for text: '{}' in DynamoDB.", textToAnalyze);

            } catch (Exception e) {
                // Log the exception and continue processing other messages.
                // In a production scenario, you might want to send this to a Dead-Letter Queue (DLQ).
                logger.error("Error processing SQS message '{}': {}", message.getMessageId(), e.getMessage(), e);
            }
        }

        return null; // Important for SQS event source: return Void to signal successful processing.
    }
}