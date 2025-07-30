package com.devopsgenai.app;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the MyApplication class.
 * These tests are designed to sometimes pass and sometimes fail
 * based on a toggle mechanism controlled by a system property,
 * simulating dynamic build outcomes.
 */
class MyApplicationTest {

    private final MyApplication app = new MyApplication();

    // System property to control test failure for demonstration purposes
    private static final boolean SIMULATE_FAILURE = Boolean.parseBoolean(System.getProperty("simulate.failure", "false"));

    @Test
    @DisplayName("Should process data correctly when input is valid")
    void testProcessData_validInput() {
        assertEquals("PROCESSED: HELLO", app.processData("hello"));
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException for null input")
    void testProcessData_nullInput() {
        assertThrows(IllegalArgumentException.class, () -> app.processData(null),
                "Expected IllegalArgumentException for null input");
    }

    @Test
    @DisplayName("Should add two numbers correctly")
    void testAddNumbers() {
        assertEquals(5, app.add(2, 3));
    }

    @Test
    @DisplayName("Simulated failure: Should return error for specific scenario if failure is simulated")
    void testSimulatedCriticalFunctionFailure() {
        // This test simulates a failure that might occur under certain conditions.
        // In a real scenario, this could be a flaky test or a bug.
        if (SIMULATE_FAILURE) {
            // Simulating a NullPointerException scenario
            // In a real app, this might come from unhandled nulls in a complex flow
            String potentiallyNullValue = null;
            try {
                // This line would actually cause the NPE in a real scenario
                // For demo, we just assert false to simulate the failure path
                assertNotNull(potentiallyNullValue, "Critical service data should not be null. Simulating NPE.");
            } catch (AssertionError e) {
                // We re-throw as an AssertionError to make Surefire report it as a failure
                throw new AssertionError("Simulated NullPointerException: User service data could not be loaded.", e);
            }
        } else {
            // If not simulating failure, this test should pass.
            assertTrue(true, "Critical function is working as expected (no simulated failure).");
        }
    }

    @Test
    @DisplayName("Simulated failure: Another test to demonstrate an assertion error if failure is simulated")
    void testSimulatedDatabaseConnectivityFailure() {
        if (SIMULATE_FAILURE) {
            // Simulate a database connection issue or an incorrect expected value
            int actualRecordsCount = 10; // Imagine this comes from a database query
            int expectedRecordsCount = 12; // Imagine this is the desired count

            // Simulating an assertion failure indicating a data mismatch or connectivity issue
            assertEquals(expectedRecordsCount, actualRecordsCount,
                    "Expected 12 database records but found " + actualRecordsCount + ". Database connectivity issue or data integrity problem.");
        } else {
            assertTrue(true, "Database connectivity is stable (no simulated failure).");
        }
    }

    @Test
    @DisplayName("Should handle division by zero by throwing ArithmeticException")
    void testDivide_byZero() {
        assertThrows(ArithmeticException.class, () -> app.divide(10, 0),
                "Expected ArithmeticException for division by zero");
    }
}