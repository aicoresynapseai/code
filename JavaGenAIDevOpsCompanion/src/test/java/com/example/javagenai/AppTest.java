package com.example.javagenai;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the App class.
 * GenAI assistants are exceptionally good at:
 * - Generating initial test method stubs based on public methods.
 * - Suggesting comprehensive test cases, including edge cases and negative scenarios.
 * - Helping write assertions and setup/teardown logic.
 * - Identifying missing test coverage.
 */
class AppTest {

    /**
     * Test case for the getCurrentTimestamp method.
     * GenAI can help ensure the format is correct or test against specific timezones.
     */
    @Test
    @DisplayName("Should return a non-empty timestamp string")
    void getCurrentTimestampShouldReturnString() {
        assertNotNull(App.getCurrentTimestamp(), "Timestamp should not be null");
        assertFalse(App.getCurrentTimestamp().isEmpty(), "Timestamp should not be empty");
        // GenAI could suggest a regex pattern check for the format:
        // assertTrue(App.getCurrentTimestamp().matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"), "Timestamp format should match YYYY-MM-DD HH:MM:SS");
    }

    /**
     * Test case for calculateFactorial method with valid input (0).
     * GenAI often suggests testing boundary conditions like 0 or 1.
     */
    @Test
    @DisplayName("Should return 1 for factorial of 0")
    void calculateFactorialOfZero() {
        assertEquals(1, App.calculateFactorial(0), "Factorial of 0 should be 1");
    }

    /**
     * Test case for calculateFactorial method with valid input (1).
     */
    @Test
    @DisplayName("Should return 1 for factorial of 1")
    void calculateFactorialOfOne() {
        assertEquals(1, App.calculateFactorial(1), "Factorial of 1 should be 1");
    }

    /**
     * Test case for calculateFactorial method with a positive integer.
     * GenAI can suggest multiple positive test cases (e.g., 3, 5, 10).
     */
    @Test
    @DisplayName("Should calculate factorial correctly for positive numbers")
    void calculateFactorialOfPositiveNumber() {
        assertEquals(120, App.calculateFactorial(5), "Factorial of 5 should be 120");
        assertEquals(720, App.calculateFactorial(6), "Factorial of 6 should be 720");
    }

    /**
     * Test case for calculateFactorial method with negative input (edge case).
     * GenAI is excellent at identifying and suggesting tests for invalid inputs
     * and expected exception handling.
     */
    @Test
    @DisplayName("Should throw IllegalArgumentException for negative input")
    void calculateFactorialOfNegativeNumber() {
        // Assert that an IllegalArgumentException is thrown for negative numbers
        // GenAI can help in structuring these try-catch or assertThrows blocks
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            App.calculateFactorial(-1);
        }, "Should throw IllegalArgumentException for negative input");

        assertTrue(thrown.getMessage().contains("Factorial is not defined for negative numbers"),
                "Exception message should indicate negative numbers are not allowed.");
    }
}