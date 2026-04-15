package com.learnify.gateway.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/fallback")
public class FallbackController {

    @RequestMapping("/**")
    public ResponseEntity<Map<String, String>> fallback() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(Map.of(
                        "status", "error",
                        "message", "Service is temporarily unavailable. Please try again later."
                ));
    }
}

