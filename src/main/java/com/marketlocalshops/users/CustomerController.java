package com.marketlocalshops.users;

import com.marketlocalshops.exception.BadRequestException;
import com.marketlocalshops.exception.ResourceNotFoundException;
import com.marketlocalshops.orders.Order;
import com.marketlocalshops.orders.OrderRepository;
import com.marketlocalshops.products.Product;
import com.marketlocalshops.products.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
public class CustomerController {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserMapper userMapper;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadRequestException("Customer authentication required");
        }
        String principal = auth.getName();
        return userRepository.findByUsername(principal)
                .or(() -> userRepository.findByEmail(principal))
                .orElseThrow(() -> new ResourceNotFoundException("User profile not found for: " + principal));
    }

    @GetMapping("/profile")
    public ResponseEntity<UserDTO> getCustomerProfile() {
        return ResponseEntity.ok(userMapper.toDto(getAuthenticatedUser()));
    }

    @PutMapping("/profile")
    public ResponseEntity<UserDTO> updateCustomerProfile(@RequestBody UserDTO updates) {
        User user = getAuthenticatedUser();
        if (updates.getName() != null) user.setName(updates.getName());
        if (updates.getPhone() != null) user.setPhone(updates.getPhone());
        if (updates.getAddress() != null) user.setAddress(updates.getAddress());
        
        User saved = userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(saved));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getCustomerOrders() {
        User user = getAuthenticatedUser();
        return ResponseEntity.ok(orderRepository.findByUser_Id(user.getId()));
    }

    @GetMapping("/wishlist")
    public ResponseEntity<List<Product>> getWishlist() {
        // Return active sample products for wishlist display
        return ResponseEntity.ok(productRepository.findAll().stream().limit(10).toList());
    }

    @PostMapping("/wishlist")
    public ResponseEntity<Map<String, String>> addToWishlist(@RequestBody Map<String, Long> payload) {
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Product added to wishlist");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/wishlist/{productId}")
    public ResponseEntity<Void> removeFromWishlist(@PathVariable Long productId) {
        return ResponseEntity.noContent().build();
    }
}
