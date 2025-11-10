package com.demoproject.gatewayservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/fallback/expenses")
    public ResponseEntity<String> expenseServiceFallback() {
        return ResponseEntity.ok("Expense Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/categories")
    public ResponseEntity<String> categoryServiceFallback() {
        return ResponseEntity.ok("Category Service is currently unavailable. Please try again later.");
    }

    @GetMapping("/fallback/auth")
    public ResponseEntity<String> authServiceFallback() {
        return ResponseEntity.ok("Auth Service is currently unavailable. Please try again later.");
    }
}