package com.marketlocalshops.users;

import com.marketlocalshops.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/profiles")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class ProfileController {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getProfiles(@RequestParam(required = false) String role) {
        List<User> users;
        if (role != null) {
            users = userRepository.findAll().stream()
                    .filter(u -> u.getRole().name().equalsIgnoreCase(role))
                    .collect(Collectors.toList());
        } else {
            users = userRepository.findAll();
        }
        List<UserDTO> dtos = users.stream().map(userMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getProfile(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));
        return ResponseEntity.ok(userMapper.toDto(user));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateProfile(@PathVariable Long id, @RequestBody UserDTO updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Profile not found with id: " + id));

        // Programmatic Security: Allow only the user themselves or an admin to edit their profile
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = auth.getName();
        User currentUser = userRepository.findByUsername(currentUsername)
                .or(() -> userRepository.findByEmail(currentUsername))
                .orElse(null);
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && (currentUser == null || !currentUser.getId().equals(id))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access denied: You can only edit your own profile.");
        }

        if (updates.getName() != null) user.setName(updates.getName());
        if (updates.getPhone() != null) user.setPhone(updates.getPhone());
        if (updates.getAddress() != null) user.setAddress(updates.getAddress());
        
        // Only admin can promote or approve users
        if (isAdmin) {
            if (updates.getRole() != null) {
                try {
                    user.setRole(com.marketlocalshops.roles.Role.valueOf(updates.getRole().toUpperCase()));
                } catch (IllegalArgumentException e) {
                    // Ignore invalid role
                }
            }
            if (updates.getIsApproved() != null) {
                user.setIsApproved(updates.getIsApproved());
            }
        }

        User saved = userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(saved));
    }
}
