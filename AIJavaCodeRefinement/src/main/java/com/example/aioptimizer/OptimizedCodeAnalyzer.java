package com.example.aioptimizer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents an optimized version of the code found in OriginalCodeAnalyzer.
 * This version leverages Java's Streams API to achieve the same functionality
 * in a more concise, readable, and potentially more performant manner.
 *
 * This class demonstrates the kind of improvements a Generative AI might suggest
 * when asked to optimize or refactor existing Java code.
 */
public class OptimizedCodeAnalyzer {

    /**
     * Processes a list of strings:
     * 1. Filters strings that have a length greater than the given minLength.
     * 2. Converts the filtered strings to uppercase.
     * 3. Calculates the sum of lengths of the processed strings.
     *
     * This method uses Java Streams for a more functional and concise approach.
     *
     * @param inputStrings The list of strings to process.
     * @param minLength The minimum length for a string to be considered.
     * @return The sum of lengths of the processed (filtered and uppercased) strings.
     */
    public long processAndCalculateSum(List<String> inputStrings, int minLength) {
        // Using Java Streams to perform filtering, mapping, and reduction (summing lengths)
        // This chain of operations is often more readable and expresses intent clearly.
        return inputStrings.stream() // Create a stream from the input list
                .filter(str -> str != null && str.length() > minLength) // Filter out strings not meeting criteria
                .map(String::toUpperCase) // Transform remaining strings to uppercase
                .mapToLong(String::length) // Map each string to its length (as a long)
                .sum(); // Sum all the lengths
    }
}