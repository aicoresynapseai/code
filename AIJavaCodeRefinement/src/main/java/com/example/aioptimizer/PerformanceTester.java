package com.example.aioptimizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Main class to demonstrate and compare the performance of the original
 * and AI-optimized Java code. It generates a large dataset and runs
 * both versions of the processing logic, measuring their execution times.
 */
public class PerformanceTester {

    private static final int NUM_STRINGS = 1_000_000; // Number of strings in the test dataset
    private static final int MIN_LENGTH = 5;         // Minimum length for filtering
    private static final int WARMUP_ITERATIONS = 5;  // Warm-up runs to allow JIT compilation
    private static final int MEASUREMENT_ITERATIONS = 10; // Actual measurement runs

    public static void main(String[] args) {
        System.out.println("Starting Java Code Optimization Showcase with Generative AI Concept...");

        // 1. Generate a large dataset for testing
        List<String> testStrings = generateRandomStrings(NUM_STRINGS);
        System.out.println(String.format("Generated %d random strings for testing.", NUM_STRINGS));

        OriginalCodeAnalyzer originalAnalyzer = new OriginalCodeAnalyzer();
        OptimizedCodeAnalyzer optimizedAnalyzer = new OptimizedCodeAnalyzer();

        // 2. Warm-up phase for JIT compilation
        System.out.println("\n--- Warm-up Phase (allowing JVM JIT compilation) ---");
        for (int i = 0; i < WARMUP_ITERATIONS; i++) {
            originalAnalyzer.processAndCalculateSum(testStrings, MIN_LENGTH);
            optimizedAnalyzer.processAndCalculateSum(testStrings, MIN_LENGTH);
        }
        System.out.println("Warm-up complete.");

        // 3. Measure performance of Original Code
        System.out.println("\n--- Measuring Original Code Performance ---");
        long originalTotalTime = 0;
        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            long result = originalAnalyzer.processAndCalculateSum(testStrings, MIN_LENGTH);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // milliseconds
            originalTotalTime += duration;
            System.out.println(String.format("Original Run %d: %d ms (Result: %d)", i + 1, duration, result));
        }
        long originalAverageTime = originalTotalTime / MEASUREMENT_ITERATIONS;
        System.out.println(String.format("Average time for Original Code: %d ms", originalAverageTime));

        // 4. Measure performance of AI-Optimized Code
        System.out.println("\n--- Measuring AI-Optimized Code Performance ---");
        long optimizedTotalTime = 0;
        for (int i = 0; i < MEASUREMENT_ITERATIONS; i++) {
            long startTime = System.nanoTime();
            long result = optimizedAnalyzer.processAndCalculateSum(testStrings, MIN_LENGTH);
            long endTime = System.nanoTime();
            long duration = (endTime - startTime) / 1_000_000; // milliseconds
            optimizedTotalTime += duration;
            System.out.println(String.format("Optimized Run %d: %d ms (Result: %d)", i + 1, duration, result));
        }
        long optimizedAverageTime = optimizedTotalTime / MEASUREMENT_ITERATIONS;
        System.out.println(String.format("Average time for AI-Optimized Code: %d ms", optimizedAverageTime));

        // 5. Display comparison results
        System.out.println("\n--- Performance Summary ---");
        System.out.println(String.format("Original Code Average: %d ms", originalAverageTime));
        System.out.println(String.format("AI-Optimized Code Average: %d ms", optimizedAverageTime));

        if (optimizedAverageTime < originalAverageTime) {
            long improvement = originalAverageTime - optimizedAverageTime;
            double percentageImprovement = (double) improvement / originalAverageTime * 100;
            System.out.println(String.format("AI-Optimized Code is faster by %d ms (%.2f%% improvement).",
                    improvement, percentageImprovement));
        } else if (optimizedAverageTime > originalAverageTime) {
            System.out.println("AI-Optimized Code was slower or similar. (This can happen in specific scenarios or with small datasets)");
        } else {
            System.out.println("Both versions performed similarly.");
        }

        System.out.println("\nDemonstration complete. Observe the difference in execution times and code structure.");
    }

    /**
     * Helper method to generate a list of random strings for testing.
     *
     * @param count The number of strings to generate.
     * @return A list of randomly generated strings.
     */
    private static List<String> generateRandomStrings(int count) {
        List<String> strings = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            int length = ThreadLocalRandom.current().nextInt(1, 20); // String length between 1 and 19
            StringBuilder sb = new StringBuilder(length);
            for (int j = 0; j < length; j++) {
                sb.append((char) ('a' + ThreadLocalRandom.current().nextInt(26)));
            }
            strings.add(sb.toString());
        }
        return strings;
    }
}