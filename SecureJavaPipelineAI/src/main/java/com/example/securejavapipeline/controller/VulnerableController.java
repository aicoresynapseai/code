package com.example.securejavapipeline.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Base64;
import java.util.Map;

/**
 * This controller contains examples of code with intentional vulnerabilities
 * to demonstrate how static analysis tools like SonarQube (and potentially AI/ML plugins)
 * would identify them.
 */
@RestController
@Slf4j
public class VulnerableController {

    private static final String JDBC_URL = "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1"; // In-memory H2 for demo

    /**
     * Vulnerability 1: Command Injection
     * This endpoint directly executes a command based on user input, which is a severe vulnerability.
     * Traditional SAST tools like SonarQube will easily flag this.
     * AI could potentially detect more complex command injection scenarios where the command is constructed
     * through multiple, seemingly innocuous steps or encoded.
     */
    @GetMapping("/executeCommand")
    public ResponseEntity<String> executeCommand(@RequestParam String command) {
        try {
            log.info("Executing command: {}", command);
            // CWE-78: Improper Neutralization of Special Elements used in an OS Command ('OS Command Injection')
            // This is a direct command injection vulnerability.
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
            return ResponseEntity.ok("Command executed: " + command);
        } catch (IOException | InterruptedException e) {
            log.error("Error executing command", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * Vulnerability 2: SQL Injection (simulated)
     * This method shows a classic SQL injection pattern. While not a real database interaction
     * in this simple example, it demonstrates the pattern.
     * Traditional SAST excels here. AI might detect issues in ORM queries or complex dynamic query builders
     * that are harder for regex-based rules to catch.
     */
    @GetMapping("/userLookup")
    public ResponseEntity<String> userLookup(@RequestParam String userId) {
        String query = "SELECT * FROM users WHERE id = '" + userId + "'"; // CWE-89: Improper Neutralization of Special Elements used in an SQL Command ('SQL Injection')
        log.info("Simulating SQL query: {}", query);
        try (Connection conn = DriverManager.getConnection(JDBC_URL);
             Statement stmt = conn.createStatement()) {
            // In a real app, you'd initialize H2 table here for a meaningful query
            // For demo purposes, we'll just log and return.
            stmt.execute("CREATE TABLE IF NOT EXISTS users (id VARCHAR(255), name VARCHAR(255));");
            stmt.execute("INSERT INTO users (id, name) VALUES ('1', 'Alice');");
            stmt.execute("INSERT INTO users (id, name) VALUES ('2', 'Bob');");

            ResultSet rs = stmt.executeQuery(query); // Vulnerable line
            StringBuilder result = new StringBuilder("Found users: ");
            while (rs.next()) {
                result.append(rs.getString("name")).append(" ");
            }
            return ResponseEntity.ok(result.toString());
        } catch (SQLException e) {
            log.error("Error with SQL query", e);
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /**
     * Vulnerability 3: Insecure Deserialization (Jackson, simulated with type issues)
     * This endpoint demonstrates a potential insecure deserialization vulnerability.
     * If an attacker can control the input to ObjectMapper without proper configuration,
     * they could potentially execute arbitrary code by supplying malicious gadget chains.
     * Modern SonarQube rules for Jackson and deserialization exist.
     * AI/ML plugins would be particularly strong here, as they can track data flow from untrusted sources
     * through deserialization mechanisms and identify the *context* that makes it exploitable,
     * even if the `enableDefaultTyping` (which is explicitly unsafe) isn't used.
     * AI could infer exploitability from chained method calls or complex object graphs.
     */
    @PostMapping("/processData")
    public ResponseEntity<String> processData(@RequestBody String data) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            // This line is often flagged by SAST.
            // Even without enableDefaultTyping, complex gadget chains or CVEs in libraries can exist.
            // AI could potentially identify issues even when explicit unsafe configurations are not present,
            // by analyzing the *usage* of deserialized objects.
            // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.JAVA_LANG_OBJECT); // CWE-502: Deserialization of Untrusted Data - If uncommented, this makes it explicitly dangerous.

            // Simulate deserialization of potentially untrusted data.
            // An AI/ML plugin would analyze the source of 'data', the configuration of 'mapper',
            // and how the resulting 'Map' object is used to determine exploitability.
            Map<String, Object> deserializedData = mapper.readValue(data, Map.class);
            log.info("Received and deserialized data: {}", deserializedData);

            // AI might also flag this as a "security hot-spot" if 'data' is derived from untrusted input
            // and used in a way that could lead to unexpected behavior or resource exhaustion,
            // even if not a direct RCE.
            if (deserializedData.containsKey("action") && deserializedData.get("action").equals("systemInfo")) {
                // Example of a sensitive action triggered by deserialized data
                // This pattern of "untrusted data driving sensitive actions" is where AI excels.
                String base64Cmd = (String) deserializedData.getOrDefault("cmd", "");
                if (!base64Cmd.isEmpty()) {
                    String decodedCmd = new String(Base64.getDecoder().decode(base64Cmd));
                    // Even if Runtime.exec is not called here, AI might flag the combination
                    // of untrusted deserialization and base64 decoding followed by potential
                    // reflection or file system access as a high-risk pattern.
                    log.warn("Potential AI-identified high-risk pattern: processing base64 encoded command from deserialized data.");
                    // For demonstration, let's pretend this *could* lead to a command execution
                    // if further processing allowed it.
                    // Runtime.getRuntime().exec(decodedCmd); // If this were here, it's a direct flag.
                }
            }


            return ResponseEntity.ok("Data processed successfully for: " + deserializedData.get("name"));

        } catch (IOException e) {
            log.error("Error processing data", e);
            return ResponseEntity.status(400).body("Error processing JSON: " + e.getMessage());
        } catch (ClassCastException e) {
            // This type of exception could be triggered by malicious input trying to cast objects incorrectly,
            // which AI might flag as an attempt to bypass security measures or exploit unexpected behavior.
            log.error("ClassCastException during deserialization, potential malicious input", e);
            return ResponseEntity.status(400).body("Invalid data format: " + e.getMessage());
        }
    }

    /**
     * Vulnerability 4: Hardcoded Sensitive Information (API Key)
     * This is a simple, common vulnerability. SonarQube flags this easily.
     * AI might prioritize this finding based on context (e.g., if it's an API key for a payment gateway vs. a test key).
     */
    private static final String HARDCODED_API_KEY = "sk_live_verysecretapikey123456"; // CWE-798: Use of Hard-coded Credentials

    @GetMapping("/getApiKey")
    public ResponseEntity<String> getApiKey() {
        // In a real application, this should be loaded from secure configuration management.
        return ResponseEntity.ok("API Key: " + HARDCODED_API_KEY);
    }
}