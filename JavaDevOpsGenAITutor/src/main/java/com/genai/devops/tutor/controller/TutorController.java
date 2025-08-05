package com.genai.devops.tutor.controller;

import com.genai.devops.tutor.service.GenAITutorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST Controller for the GenAI DevOps Tutor application.
 * Handles incoming HTTP requests related to tutoring functionalities.
 */
@RestController // Marks this class as a REST Controller, handling web requests.
@RequestMapping("/api/tutor") // Base path for all endpoints in this controller.
public class TutorController {

    // Injects the GenAITutorService to handle the business logic and AI simulation.
    private final GenAITutorService genAITutorService;

    @Autowired // Marks a constructor, field, or method for Spring to inject dependencies.
    public TutorController(GenAITutorService genAITutorService) {
        this.genAITutorService = genAITutorService;
    }

    /**
     * Endpoint for the interactive chatbot.
     * Takes a user query and returns a simulated GenAI response.
     *
     * @param request A map containing the user's "query".
     * @return A ResponseEntity containing the AI's response as a map.
     */
    @PostMapping("/chat") // Maps HTTP POST requests to /api/tutor/chat.
    public ResponseEntity<Map<String, String>> chatWithAI(@RequestBody Map<String, String> request) {
        String query = request.get("query");
        if (query == null || query.trim().isEmpty()) {
            // Return a bad request if the query is empty.
            return ResponseEntity.badRequest().body(Map.of("error", "Query cannot be empty."));
        }
        // Calls the service to generate a response based on the query.
        String aiResponse = genAITutorService.generateChatResponse(query);
        // Returns the response wrapped in a Map for JSON serialization.
        return ResponseEntity.ok(Map.of("response", aiResponse));
    }

    /**
     * Endpoint for explaining a given code snippet.
     * Takes a code string and returns a simulated GenAI explanation.
     *
     * @param request A map containing the "code" snippet to explain.
     * @return A ResponseEntity containing the AI's explanation as a map.
     */
    @PostMapping("/explainCode") // Maps HTTP POST requests to /api/tutor/explainCode.
    public ResponseEntity<Map<String, String>> explainCode(@RequestBody Map<String, String> request) {
        String code = request.get("code");
        if (code == null || code.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Code snippet cannot be empty."));
        }
        // Calls the service to explain the code.
        String explanation = genAITutorService.explainCodeSnippet(code);
        return ResponseEntity.ok(Map.of("explanation", explanation));
    }

    /**
     * Endpoint for generating an onboarding guide for a specific topic.
     * Takes a topic string and returns a simulated GenAI onboarding guide.
     *
     * @param request A map containing the "topic" for the guide.
     * @return A ResponseEntity containing the AI's generated guide as a map.
     */
    @PostMapping("/onboard") // Maps HTTP POST requests to /api/tutor/onboard.
    public ResponseEntity<Map<String, String>> generateOnboardingGuide(@RequestBody Map<String, String> request) {
        String topic = request.get("topic");
        if (topic == null || topic.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Onboarding topic cannot be empty."));
        }
        // Calls the service to generate the onboarding guide.
        String guide = genAITutorService.generateOnboardingGuide(topic);
        return ResponseEntity.ok(Map.of("guide", guide));
    }

    /**
     * Endpoint to retrieve all pre-defined FAQs.
     *
     * @return A ResponseEntity containing a list of FAQ maps (question and answer).
     */
    @GetMapping("/faq") // Maps HTTP GET requests to /api/tutor/faq.
    public ResponseEntity<List<Map<String, String>>> getAllFaqs() {
        // Calls the service to get the list of all FAQs.
        List<Map<String, String>> faqs = genAITutorService.getFAQs();
        return ResponseEntity.ok(faqs);
    }

    /**
     * Endpoint to ask a question related to FAQs, potentially with AI enhancement.
     *
     * @param request A map containing the user's "question".
     * @return A ResponseEntity containing the AI-enhanced answer as a map.
     */
    @PostMapping("/faq/ask") // Maps HTTP POST requests to /api/tutor/faq/ask.
    public ResponseEntity<Map<String, String>> askFaqQuestion(@RequestBody Map<String, String> request) {
        String question = request.get("question");
        if (question == null || question.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Question cannot be empty."));
        }
        // Calls the service to answer the FAQ question, which might involve matching or generating.
        String answer = genAITutorService.answerFAQQuestion(question);
        return ResponseEntity.ok(Map.of("answer", answer));
    }
}