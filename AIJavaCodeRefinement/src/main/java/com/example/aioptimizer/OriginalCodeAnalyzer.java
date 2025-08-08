package com.example.aioptimizer;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a piece of Java code that might be considered less optimal or more verbose.
 * This class processes a list of strings, filters them, transforms them, and calculates
 * a sum based on their lengths, using traditional imperative loops.
 *
 * A Generative AI might identify this as a candidate for refactoring to improve conciseness and performance.
 */
public class OriginalCodeAnalyzer {

    /**
     * Processes a list of strings:
     * 1. Filters strings that have a length greater than the given minLength.
     * 2. Converts the filtered strings to uppercase.
     * 3. Calculates the sum of lengths of the processed strings.
     *
     * This method uses a traditional for-loop and explicit conditional checks.
     *
     * @param inputStrings The list of strings to process.
     * @param minLength The minimum length for a string to be considered.
     * @return The sum of lengths of the processed (filtered and uppercased) strings.
     */
    public long processAndCalculateSum(List<String> inputStrings, int minLength) {
        // Initialize a list to hold filtered and transformed strings
        List<String> filteredAndTransformed = new ArrayList<>();
        long totalLength = 0;

        // Iterate through each string in the input list
        for (String str : inputStrings) {
            // Check if the string meets the minimum length criteria
            if (str != null && str.length() > minLength) {
                // Transform the string to uppercase
                String transformedStr = str.toUpperCase();
                // Add to our temporary list
                filteredAndTransformed.add(transformedStr);
            }
        }

        // Now iterate through the filtered and transformed list to calculate the sum of lengths
        for (String processedStr : filteredAndTransformed) {
            totalLength += processedStr.length();
        }

        return totalLength;
    }
}