package com.aisentinel.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * A simple REST controller demonstrating a secured resource.
 * All endpoints in this controller are subject to both Spring Security authentication
 * and our custom AI security interceptor.
 */
@RestController
@RequestMapping("/secure") // Base path for all endpoints in this controller
public class SecureResourceController {

    /**
     * An example endpoint that returns a secure message.
     * This endpoint will be protected by Spring Security and our AI security interceptor.
     * @return A greeting message.
     */
    @GetMapping("/data")
    public ResponseEntity<String> getSecureData() {
        // This message will only be returned if the request passes both
        // Spring Security's authentication and the AI Guardian's check.
        return ResponseEntity.ok("Hello, this is secure data protected by AI Sentinel!");
    }

    /**
     * Another example endpoint with a path variable and request parameter.
     * This allows the AI Guardian to analyze different parts of the request (URI, query params).
     *
     * To test AI detection:
     * - Try accessing: /secure/info/123?query=SQL_INJECTION_ATTACK
     * - Or a path that might trigger traversal: /secure/info/../../etc/passwd
     *
     * @param id A path variable.
     * @param query A request parameter.
     * @return A customized secure message.
     */
    @GetMapping("/info/{id}")
    public ResponseEntity<String> getSecureInfo(@PathVariable String id, @RequestParam(required = false) String query) {
        String responseMessage = String.format("Accessed secure info for ID: %s", id);
        if (query != null && !query.isEmpty()) {
            responseMessage += String.format(" with query: '%s'", query);
        }
        return ResponseEntity.ok(responseMessage + ". AI Guardian approved this request.");
    }

    /**
     * A public endpoint that doesn't require authentication but could still be intercepted by AI for general threat detection.
     */
    @GetMapping("/public/status")
    public ResponseEntity<String> getPublicStatus() {
        return ResponseEntity.ok("Public status check. AI Guardian still observing.");
    }
}