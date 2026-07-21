package com.marketlocalshops.auth;

import com.marketlocalshops.roles.Role;
import com.marketlocalshops.users.User;
import com.marketlocalshops.users.UserRepository;
import com.marketlocalshops.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils,
                          UserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@jakarta.validation.Valid @RequestBody AuthRequest request) {
        try {
            String loginIdentifier = request.getEmail().trim();
            
            // Search database for the user either by email or username
            User user = userRepository.findByEmail(loginIdentifier.toLowerCase())
                    .or(() -> userRepository.findByUsername(loginIdentifier))
                    .orElseThrow(() -> new RuntimeException("Invalid email/username or password"));

            // Authenticate credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );

            // Generate JWT token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            final String jwt = jwtUtils.generateToken(userDetails);
            final String refreshToken = jwtUtils.generateRefreshToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(
                    jwt,
                    refreshToken,
                    "Bearer",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid credentials");
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@jakarta.validation.Valid @RequestBody RegisterRequest request) {
        try {
            String email = request.getEmail().trim().toLowerCase();
            if (userRepository.findByEmail(email).isPresent()) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email is already in use");
            }

            // Standardize username
            String username = request.getName();
            if (username == null || username.trim().isEmpty()) {
                username = email.split("@")[0];
            } else {
                username = username.trim().replaceAll("\\s+", "_").toLowerCase();
            }

            // Ensure username uniqueness
            String baseUsername = username;
            int count = 1;
            while (userRepository.findByUsername(username).isPresent()) {
                username = baseUsername + "_" + count;
                count++;
            }

            Role userRole;
            try {
                userRole = Role.valueOf(request.getRole().toUpperCase());
            } catch (Exception e) {
                userRole = Role.CUSTOMER; // Default role
            }

            User user = User.builder()
                    .username(username)
                    .email(email)
                    .password(passwordEncoder.encode(request.getPassword()))
                    .role(userRole)
                    .isApproved(true)
                    .build();

            userRepository.save(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PostMapping({"/refresh-token", "/refresh"})
    public ResponseEntity<?> refreshToken(@RequestBody java.util.Map<String, String> body) {
        String token = body.get("refreshToken");
        if (token == null || token.isBlank()) {
            token = body.get("token");
        }
        if (token == null || token.isBlank()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Refresh token is required");
        }

        try {
            String username = jwtUtils.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtils.validateToken(token, userDetails) || !jwtUtils.isTokenExpired(token)) {
                String newJwt = jwtUtils.generateToken(userDetails);
                String newRefreshToken = jwtUtils.generateRefreshToken(userDetails);
                User user = userRepository.findByUsername(username)
                        .or(() -> userRepository.findByEmail(username))
                        .orElse(null);

                return ResponseEntity.ok(new AuthResponse(
                        newJwt,
                        newRefreshToken,
                        "Bearer",
                        user != null ? user.getId() : null,
                        username,
                        user != null ? user.getEmail() : null,
                        user != null ? user.getRole().name() : "CUSTOMER"
                ));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired refresh token");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token: " + e.getMessage());
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() 
                || "anonymousUser".equals(authentication.getPrincipal())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        return ResponseEntity.ok("Logged out successfully");
    }
}
