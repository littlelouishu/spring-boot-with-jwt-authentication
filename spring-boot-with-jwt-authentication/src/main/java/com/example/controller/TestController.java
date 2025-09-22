package com.example.controller;

import com.example.security.user.User;
import com.example.common.DefaultResponseHeaders;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class TestController {
    @GetMapping("/public")
    @DefaultResponseHeaders
    public ResponseEntity<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected")
    @DefaultResponseHeaders
    public ResponseEntity<Map<String, Object>> protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("message", "This is a protected endpoint");
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        response.put("timestamp", System.currentTimeMillis());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/admin")
    @DefaultResponseHeaders
    public ResponseEntity<Map<String, String>> adminEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is an admin endpoint");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity.ok(response);
    }
}
