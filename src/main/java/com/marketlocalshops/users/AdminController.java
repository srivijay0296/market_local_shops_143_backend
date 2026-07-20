package com.marketlocalshops.users;

import com.marketlocalshops.products.ProductRepository;
import com.marketlocalshops.shops.ShopRepository;
import com.marketlocalshops.roles.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final UserRepository userRepository;
    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final UserMapper userMapper;
    private final UserService userService;

    private final com.marketlocalshops.markets.MarketRepository marketRepository;
    private final com.marketlocalshops.orders.OrderRepository orderRepository;

    @GetMapping("/analytics")
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        long totalUsers = userRepository.count();
        long totalShops = shopRepository.count();
        long totalProducts = productRepository.count();
        long totalMarkets = marketRepository.count();
        long totalOrders = orderRepository.count();
        
        double totalRevenue = orderRepository.findAll().stream()
                .mapToDouble(com.marketlocalshops.orders.Order::getTotalAmount)
                .sum();

        long pendingShops = shopRepository.findByStatus("pending").size();
        long pendingProducts = productRepository.findAll().stream()
                .filter(p -> p.getIsApproved() != null && !p.getIsApproved())
                .count();

        long admins = userRepository.findAll().stream().filter(u -> u.getRole() == Role.ADMIN).count();
        long sellers = userRepository.findAll().stream().filter(u -> u.getRole() == Role.SELLER).count();
        long buyers = userRepository.findAll().stream().filter(u -> u.getRole() == Role.CUSTOMER).count();
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalShops", totalShops);
        stats.put("totalProducts", totalProducts);
        stats.put("totalMarkets", totalMarkets);
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("pendingShops", pendingShops);
        stats.put("pendingProducts", pendingProducts);
        stats.put("sellers", sellers);
        stats.put("buyers", buyers);
        stats.put("admins", admins);
        stats.put("activeUsers", totalUsers);

        // Include trend chart arrays for frontend data visualization
        stats.put("salesTrend", List.of(
            Map.of("name", "Jan", "revenue", totalRevenue * 0.08),
            Map.of("name", "Feb", "revenue", totalRevenue * 0.12),
            Map.of("name", "Mar", "revenue", totalRevenue * 0.15),
            Map.of("name", "Apr", "revenue", totalRevenue * 0.10),
            Map.of("name", "May", "revenue", totalRevenue * 0.20),
            Map.of("name", "Jun", "revenue", totalRevenue * 0.35)
        ));

        stats.put("categoryShares", List.of(
            Map.of("name", "Groceries", "value", 40),
            Map.of("name", "Electronics", "value", 25),
            Map.of("name", "Clothing", "value", 20),
            Map.of("name", "Accessories", "value", 15)
        ));
        
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<Object> getUsers(
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,desc") String sort) {
        
        Role roleEnum = null;
        if (role != null && !role.trim().isEmpty()) {
            try {
                roleEnum = Role.valueOf(role.trim().toUpperCase());
            } catch (IllegalArgumentException e) {
                // Ignore invalid role
            }
        }

        if (page != null && size != null) {
            String[] sortParts = sort.split(",");
            Sort.Direction direction = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])) 
                    ? Sort.Direction.ASC 
                    : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortParts[0]));
            
            Page<UserDTO> userPage = userRepository.findUsersWithFilters(
                    roleEnum != null ? com.marketlocalshops.roles.Role.valueOf(roleEnum.name()) : null,
                    search,
                    pageable
            ).map(userMapper::toDto);
            return ResponseEntity.ok(userPage);
        }

        List<UserDTO> users = userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> adminUpdateUser(@PathVariable Long id, @RequestBody UserDTO updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("User not found"));
        
        if (updates.getUsername() != null) {
            user.setUsername(updates.getUsername());
        }
        if (updates.getEmail() != null) {
            user.setEmail(updates.getEmail());
        }
        if (updates.getRole() != null) {
            try {
                user.setRole(Role.valueOf(updates.getRole().toUpperCase()));
            } catch (IllegalArgumentException e) {
                // Ignore invalid role
            }
        }
        if (updates.getName() != null) {
            user.setName(updates.getName());
        }
        if (updates.getPhone() != null) {
            user.setPhone(updates.getPhone());
        }
        if (updates.getAddress() != null) {
            user.setAddress(updates.getAddress());
        }
        if (updates.getIsApproved() != null) {
            user.setIsApproved(updates.getIsApproved());
        }
        User saved = userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(saved));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/users/create")
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        return ResponseEntity.ok(userService.createUser(userDTO));
    }
}
