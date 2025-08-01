package com.example.javagenai;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Main application class for JavaGenAIDevOpsCompanion.
 * This class demonstrates a simple Java application.
 * GenAI assistants can help with:
 * - Generating new methods (e.g., for complex calculations).
 * - Refactoring existing code for better readability or performance.
 * - Adding Javadoc comments and explanations.
 * - Suggesting improvements for error handling.
 */
public class App {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Main method to run the application.
     * @param args Command line arguments (not used in this simple app).
     */
    public static void main(String[] args) {
        System.out.println("Hello from JavaGenAIDevOpsCompanion!");
        System.out.println("The current time is: " + getCurrentTimestamp());

        // Example of a method that could be generated or improved by GenAI
        int number = 5;
        long factorialResult = calculateFactorial(number);
        System.out.println("Factorial of " + number + " is: " + factorialResult);

        // GenAI could suggest adding logging here instead of System.out.println
        // Logger logger = LoggerFactory.getLogger(App.class);
        // logger.info("Application started successfully.");
    }

    /**
     * Returns the current timestamp formatted as a string.
     * GenAI can help in ensuring correct date/time formatting based on locale or requirements.
     *
     * @return A formatted string representing the current date and time.
     */
    public static String getCurrentTimestamp() {
        return LocalDateTime.now().format(FORMATTER);
    }

    /**
     * Calculates the factorial of a given non-negative integer.
     * This method can be a target for GenAI to generate unit tests or identify edge cases.
     *
     * @param n The non-negative integer for which to calculate the factorial.
     * @return The factorial of n.
     * @throws IllegalArgumentException if n is negative.
     */
    public static long calculateFactorial(int n) {
        // GenAI could suggest adding input validation
        if (n < 0) {
            // GenAI can help refine exception messages or types
            throw new IllegalArgumentException("Factorial is not defined for negative numbers.");
        }
        if (n == 0 || n == 1) {
            return 1;
        }
        long result = 1;
        for (int i = 2; i <= n; i++) {
            result *= i;
        }
        return result;
    }
}