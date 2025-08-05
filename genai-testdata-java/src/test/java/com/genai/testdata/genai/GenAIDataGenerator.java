package com.genai.testdata.genai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.genai.testdata.model.User;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

// This class simulates interaction with a Generative AI model for test data generation.
// In a real application, this would involve API calls to services like OpenAI, Google Gemini, etc.
// For this example, we generate diverse data programmatically to mimic GenAI's output capabilities.
public class GenAIDataGenerator {

    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final Random random = new Random();

    // Sample data pools to mimic diverse GenAI outputs
    private static final List<String> FIRST_NAMES = Arrays.asList("Alice", "Bob", "Charlie", "Diana", "Eve", "Frank", "Grace", "Heidi", "Ivan", "Judy", "Liam", "Olivia", "Noah", "Emma", "Ava", "Sophia", "Jackson", "Lucas", "Aiden", "Isabella", "Mia", "Harper", "Evelyn", "Abigail", "Emily");
    private static final List<String> LAST_NAMES = Arrays.asList("Smith", "Johnson", "Williams", "Brown", "Jones", "Garcia", "Miller", "Davis", "Rodriguez", "Martinez", "Hernandez", "Lopez", "Gonzalez", "Wilson", "Anderson", "Thomas", "Taylor", "Moore", "Jackson", "Martin");
    private static final List<String> COUNTRIES = Arrays.asList("USA", "Canada", "UK", "Australia", "Germany", "France", "Japan", "India", "Brazil", "Mexico", "China", "South Africa", "Egypt", "Spain", "Italy");
    private static final List<String> EMAIL_DOMAINS = Arrays.asList("example.com", "mail.net", "test.org", "genai.dev", "company.co");

    /**
     * Simulates prompting a GenAI model to generate a list of user profiles.
     * In a real scenario, the 'prompt' would be sent to the GenAI API,
     * and the API would return a JSON string based on the prompt.
     *
     * @param prompt The natural language prompt for the GenAI model (e.g., "Generate 5 user profiles with diverse ages and countries").
     * @param count The number of user profiles to generate.
     * @return A list of User objects.
     */
    public static List<User> generateUserData(String prompt, int count) {
        System.out.println("Simulating GenAI data generation for prompt: '" + prompt + "' with count: " + count);

        // --- REAL GENAI INTEGRATION CONCEPT ---
        // In a real application, you would do something like:
        /*
        try {
            // 1. Prepare your API client (e.g., for OpenAI, Google Gemini, etc.)
            GenAIApiClient client = new GenAIApiClient("YOUR_API_KEY");

            // 2. Define the desired output format (e.g., JSON schema)
            String jsonSchema = """
            {
              "type": "array",
              "items": {
                "type": "object",
                "properties": {
                  "id": {"type": "string"},
                  "firstName": {"type": "string"},
                  "lastName": {"type": "string"},
                  "email": {"type": "string", "format": "email"},
                  "age": {"type": "integer", "minimum": 1, "maximum": 100},
                  "country": {"type": "string"}
                },
                "required": ["id", "firstName", "lastName", "email", "age", "country"]
              }
            }
            """;

            // 3. Formulate the prompt, potentially including instructions for format
            String fullPrompt = prompt + "\nOutput " + count + " entries in JSON format conforming to this schema:\n" + jsonSchema;

            // 4. Call the GenAI API
            String jsonResponse = client.generateText(fullPrompt, /* optional parameters like temperature, model */);

            // 5. Parse the JSON response into a List<User>
            return objectMapper.readValue(jsonResponse, new TypeReference<List<User>>() {});

        } catch (IOException e) {
            System.err.println("Error calling GenAI API or parsing response: " + e.getMessage());
            // Fallback to mock data or throw exception
            return generateMockUserData(count, prompt); // Fallback to mock generation
        }
        */
        // --- END REAL GENAI INTEGRATION CONCEPT ---


        // --- MOCKED/SIMULATED GENAI DATA GENERATION FOR THIS EXAMPLE ---
        // This part programmatically generates diverse data to illustrate the *type* of output
        // a GenAI might produce based on a sophisticated prompt.
        return generateMockUserData(count, prompt);
    }

    /**
     * Generates a list of mock User objects based on some diversity rules,
     * mimicking a GenAI's ability to create varied data.
     * This is a fallback/simulation for when a real GenAI API isn't called.
     *
     * @param count The number of users to generate.
     * @param prompt A conceptual prompt that influences diversity (used here to show how different prompts *could* lead to different data mixes).
     * @return A list of generated User objects.
     */
    private static List<User> generateMockUserData(int count, String prompt) {
        List<User> users = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String id = UUID.randomUUID().toString();
            String firstName = FIRST_NAMES.get(random.nextInt(FIRST_NAMES.size()));
            String lastName = LAST_NAMES.get(random.nextInt(LAST_NAMES.size()));
            String email = (firstName.toLowerCase() + "." + lastName.toLowerCase() + i + "@" + EMAIL_DOMAINS.get(random.nextInt(EMAIL_DOMAINS.size()))).replace(" ", "");

            int age;
            String country;

            // Introduce diversity based on prompt keywords (simulating GenAI's understanding)
            if (prompt.toLowerCase().contains("young")) {
                age = 18 + random.nextInt(10); // 18-27
            } else if (prompt.toLowerCase().contains("senior") || prompt.toLowerCase().contains("older")) {
                age = 60 + random.nextInt(30); // 60-89
            } else if (prompt.toLowerCase().contains("minor") || prompt.toLowerCase().contains("under 18")) {
                age = 5 + random.nextInt(13); // 5-17
            } else {
                age = 18 + random.nextInt(60); // Default: 18-77
            }

            if (prompt.toLowerCase().contains("eu countries")) {
                country = Arrays.asList("Germany", "France", "Italy", "Spain").get(random.nextInt(4));
            } else if (prompt.toLowerCase().contains("asian countries")) {
                country = Arrays.asList("Japan", "India", "China").get(random.nextInt(3));
            } else {
                country = COUNTRIES.get(random.nextInt(COUNTRIES.size()));
            }

            users.add(new User(id, firstName, lastName, email, age, country));
        }
        return users;
    }
}