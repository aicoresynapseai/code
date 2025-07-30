package com.devopsgenai.app;

/**
 * A simple application class.
 * This class doesn't do much, but serves as part of the Java project
 * that would be built and tested in a real CI/CD pipeline.
 */
public class MyApplication {

    /**
     * A sample method that might perform some operation.
     * @param input A string input.
     * @return A processed string.
     */
    public String processData(String input) {
        if (input == null || input.isEmpty()) {
            // Simulate a potential error condition leading to a NullPointerException in tests
            throw new IllegalArgumentException("Input cannot be null or empty.");
        }
        return "Processed: " + input.toUpperCase();
    }

    /**
     * Another sample method to simulate complex logic.
     * @param a first number
     * @param b second number
     * @return sum of a and b
     */
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * A method that could potentially cause an error, e.g., division by zero
     * if not handled, or other runtime exceptions.
     */
    public double divide(int numerator, int denominator) {
        if (denominator == 0) {
            // This could be caught, but for a demo, we might let it throw
            throw new ArithmeticException("Cannot divide by zero.");
        }
        return (double) numerator / denominator;
    }
}