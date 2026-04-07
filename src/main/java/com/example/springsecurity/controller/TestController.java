package com.example.springsecurity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    // Accessible by both USER and ADMIN
    @GetMapping("/user/hello")
    public ResponseEntity<String> userHello(Authentication authentication) {
        return ResponseEntity.ok("Hello " + authentication.getName() + "! You have USER access.");
    }

    // Accessible by ADMIN only
    @GetMapping("/admin/hello")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> adminHello(Authentication authentication) {
        return ResponseEntity.ok("Hello " + authentication.getName() + "! You have ADMIN access.");
    }

    // Accessible by any authenticated user
    @GetMapping("/profile")
    public ResponseEntity<String> profile(Authentication authentication) {
        return ResponseEntity.ok("Logged in as: " + authentication.getName()
                + " | Roles: " + authentication.getAuthorities());
    }
}
