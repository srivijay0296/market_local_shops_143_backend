package com.marketlocalshops.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        // Placeholder for actual authentication logic
        return ResponseEntity.ok(new AuthResponse(
                "dummy-jwt-token",
                "Bearer",
                1L,
                "admin",
                request.getEmail(),
                "ADMIN"
        ));
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody AuthRequest request) {
        // Placeholder for actual registration logic
        return ResponseEntity.ok("User registered successfully");
    }
}
