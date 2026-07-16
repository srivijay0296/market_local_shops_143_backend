package com.marketlocalshops.auth;

import com.marketlocalshops.roles.Role;
import com.marketlocalshops.users.User;
import com.marketlocalshops.users.UserRepository;
import com.marketlocalshops.security.JwtUtils;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            String loginIdentifier = request.getEmail().trim().toLowerCase();
            
            // Search database for the user either by email or username
            User user = userRepository.findByEmail(loginIdentifier)
                    .or(() -> userRepository.findByUsername(loginIdentifier))
                    .orElseThrow(() -> new RuntimeException("Invalid email/username or password"));

            // Authenticate credentials
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), request.getPassword())
            );

            // Generate JWT token
            final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
            final String jwt = jwtUtils.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse(
                    jwt,
                    "Bearer",
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getRole().name()
            ));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
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
                    .build();

            userRepository.save(user);
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
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
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(user);
    }
}
