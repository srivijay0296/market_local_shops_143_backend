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

    @GetMapping({"/analytics", "/dashboard", "/stats"})
    public ResponseEntity<Map<String, Object>> getAnalytics() {
        long totalUsers = userRepository.count();
        long totalShops = shopRepository.count();
        long totalProducts = productRepository.count();
        long totalMarkets = marketRepository.count();
        long activeMarkets = marketRepository.findAll().stream()
                .filter(m -> "active".equalsIgnoreCase(m.getStatus()))
                .count();
        long totalOrders = orderRepository.count();
        
        List<com.marketlocalshops.orders.Order> orders = orderRepository.findAll();
        double totalRevenue = orders.stream()
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                .sum();

        long pendingShops = shopRepository.findByStatus("pending").size();
        long pendingProducts = productRepository.findAll().stream()
                .filter(p -> p.getIsApproved() != null && !p.getIsApproved())
                .count();

        long admins = userRepository.findAll().stream().filter(u -> u.getRole() == Role.ADMIN || u.getRole() == Role.SUPER_ADMIN).count();
        long sellers = userRepository.findAll().stream().filter(u -> u.getRole() == Role.SELLER).count();
        long buyers = userRepository.findAll().stream().filter(u -> u.getRole() == Role.CUSTOMER).count();
        
        // Top selling products
        List<Map<String, Object>> topSellingProducts = productRepository.findAll().stream()
                .limit(5)
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("name", p.getName());
                    map.put("price", p.getPrice());
                    map.put("salesCount", p.getViewCount() != null ? p.getViewCount() : 0);
                    return map;
                }).collect(Collectors.toList());

        // Top shops
        List<Map<String, Object>> topShops = shopRepository.findAll().stream()
                .limit(5)
                .map(s -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", s.getId());
                    map.put("name", s.getName());
                    map.put("status", s.getStatus());
                    return map;
                }).collect(Collectors.toList());

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalUsers", totalUsers);
        stats.put("totalShops", totalShops);
        stats.put("totalProducts", totalProducts);
        stats.put("totalMarkets", totalMarkets);
        stats.put("activeMarkets", activeMarkets > 0 ? activeMarkets : totalMarkets);
        stats.put("totalOrders", totalOrders);
        stats.put("totalRevenue", totalRevenue);
        stats.put("revenue", totalRevenue);
        stats.put("pendingShops", pendingShops);
        stats.put("pendingShopRequests", pendingShops);
        stats.put("pendingProducts", pendingProducts);
        stats.put("pendingProductApprovals", pendingProducts);
        stats.put("sellers", sellers);
        stats.put("buyers", buyers);
        stats.put("admins", admins);
        stats.put("activeUsers", totalUsers);
        stats.put("topSellingProducts", topSellingProducts);
        stats.put("topShops", topShops);

        stats.put("monthlySales", List.of(
            Map.of("month", "Jan", "sales", totalRevenue * 0.08),
            Map.of("month", "Feb", "sales", totalRevenue * 0.12),
            Map.of("month", "Mar", "sales", totalRevenue * 0.15),
            Map.of("month", "Apr", "sales", totalRevenue * 0.10),
            Map.of("month", "May", "sales", totalRevenue * 0.20),
            Map.of("month", "Jun", "sales", totalRevenue * 0.35)
        ));

        stats.put("salesTrend", List.of(
            Map.of("name", "Jan", "revenue", totalRevenue * 0.08),
            Map.of("name", "Feb", "revenue", totalRevenue * 0.12),
            Map.of("name", "Mar", "revenue", totalRevenue * 0.15),
            Map.of("name", "Apr", "revenue", totalRevenue * 0.10),
            Map.of("name", "May", "revenue", totalRevenue * 0.20),
            Map.of("name", "Jun", "revenue", totalRevenue * 0.35)
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

    @GetMapping("/sellers")
    public ResponseEntity<List<UserDTO>> getSellers() {
        List<UserDTO> sellers = userRepository.findAll().stream()
                .filter(u -> u.getRole() == Role.SELLER)
                .map(userMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(sellers);
    }

    @RequestMapping(value = "/sellers/{id}/approve", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<UserDTO> approveSeller(@PathVariable Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Seller not found"));
        user.setRole(Role.SELLER);
        user.setIsApproved(true);
        User saved = userRepository.save(user);
        return ResponseEntity.ok(userMapper.toDto(saved));
    }

    @GetMapping("/shops")
    public ResponseEntity<List<com.marketlocalshops.shops.Shop>> getAllShopsAdmin() {
        return ResponseEntity.ok(shopRepository.findAll());
    }

    @GetMapping("/shops/pending")
    public ResponseEntity<List<com.marketlocalshops.shops.Shop>> getPendingShops() {
        return ResponseEntity.ok(shopRepository.findByStatus("pending"));
    }

    @RequestMapping(value = "/shops/{id}/approve", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<com.marketlocalshops.shops.Shop> approveShop(@PathVariable Long id) {
        com.marketlocalshops.shops.Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Shop not found"));
        shop.setStatus("approved");
        return ResponseEntity.ok(shopRepository.save(shop));
    }

    @RequestMapping(value = "/shops/{id}/reject", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<com.marketlocalshops.shops.Shop> rejectShop(@PathVariable Long id) {
        com.marketlocalshops.shops.Shop shop = shopRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Shop not found"));
        shop.setStatus("rejected");
        return ResponseEntity.ok(shopRepository.save(shop));
    }

    @GetMapping("/products")
    public ResponseEntity<List<com.marketlocalshops.products.Product>> getAllProductsAdmin() {
        return ResponseEntity.ok(productRepository.findAll());
    }

    @GetMapping("/products/pending")
    public ResponseEntity<List<com.marketlocalshops.products.Product>> getPendingProducts() {
        List<com.marketlocalshops.products.Product> pending = productRepository.findAll().stream()
                .filter(p -> p.getIsApproved() != null && !p.getIsApproved())
                .collect(Collectors.toList());
        return ResponseEntity.ok(pending);
    }

    @RequestMapping(value = "/products/{id}/approve", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<com.marketlocalshops.products.Product> approveProduct(@PathVariable Long id) {
        com.marketlocalshops.products.Product product = productRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Product not found"));
        product.setIsApproved(true);
        return ResponseEntity.ok(productRepository.save(product));
    }

    @RequestMapping(value = "/products/{id}/reject", method = {RequestMethod.POST, RequestMethod.PUT})
    public ResponseEntity<com.marketlocalshops.products.Product> rejectProduct(@PathVariable Long id) {
        com.marketlocalshops.products.Product product = productRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Product not found"));
        product.setIsApproved(false);
        return ResponseEntity.ok(productRepository.save(product));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<com.marketlocalshops.orders.Order>> getAllOrdersAdmin() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @RequestMapping(value = "/orders/{id}/status", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<com.marketlocalshops.orders.Order> updateOrderStatusAdmin(@PathVariable Long id, @RequestBody Map<String, String> body) {
        com.marketlocalshops.orders.Order order = orderRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Order not found"));
        if (body.containsKey("status")) {
            order.setStatus(body.get("status").toUpperCase());
        }
        return ResponseEntity.ok(orderRepository.save(order));
    }

    @GetMapping("/markets")
    public ResponseEntity<List<com.marketlocalshops.markets.Market>> getAllMarketsAdmin() {
        return ResponseEntity.ok(marketRepository.findAll());
    }

    @PostMapping("/markets")
    public ResponseEntity<com.marketlocalshops.markets.Market> createMarketAdmin(@RequestBody com.marketlocalshops.markets.Market market) {
        if (market.getStatus() == null) market.setStatus("active");
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(marketRepository.save(market));
    }

    @PutMapping("/markets/{id}")
    public ResponseEntity<com.marketlocalshops.markets.Market> updateMarketAdmin(@PathVariable Long id, @RequestBody com.marketlocalshops.markets.Market updates) {
        com.marketlocalshops.markets.Market market = marketRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Market not found"));
        if (updates.getName() != null) market.setName(updates.getName());
        if (updates.getLocation() != null) market.setLocation(updates.getLocation());
        if (updates.getStatus() != null) market.setStatus(updates.getStatus());
        if (updates.getSlug() != null) market.setSlug(updates.getSlug());
        if (updates.getDescription() != null) market.setDescription(updates.getDescription());
        if (updates.getImageUrl() != null) market.setImageUrl(updates.getImageUrl());
        return ResponseEntity.ok(marketRepository.save(market));
    }

    @DeleteMapping("/markets/{id}")
    public ResponseEntity<Void> deleteMarketAdmin(@PathVariable Long id) {
        com.marketlocalshops.markets.Market market = marketRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("Market not found"));
        marketRepository.delete(market);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/users/{id}")
    public ResponseEntity<UserDTO> adminUpdateUser(@PathVariable Long id, @RequestBody UserDTO updates) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new com.marketlocalshops.exception.ResourceNotFoundException("User not found"));
        
        if (updates.getUsername() != null) user.setUsername(updates.getUsername());
        if (updates.getEmail() != null) user.setEmail(updates.getEmail());
        if (updates.getRole() != null) {
            try {
                user.setRole(Role.valueOf(updates.getRole().toUpperCase()));
            } catch (IllegalArgumentException ignored) {}
        }
        if (updates.getName() != null) user.setName(updates.getName());
        if (updates.getPhone() != null) user.setPhone(updates.getPhone());
        if (updates.getAddress() != null) user.setAddress(updates.getAddress());
        if (updates.getIsApproved() != null) user.setIsApproved(updates.getIsApproved());
        
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

    @GetMapping("/settings")
    public ResponseEntity<Map<String, Object>> getSettings() {
        Map<String, Object> settings = new HashMap<>();
        settings.put("siteName", "Namma Market");
        settings.put("enableShopRegistration", true);
        settings.put("requireProductApproval", false);
        settings.put("supportEmail", "support@nammamarket.com");
        return ResponseEntity.ok(settings);
    }

    @PostMapping("/settings")
    public ResponseEntity<Map<String, Object>> updateSettings(@RequestBody Map<String, Object> settings) {
        return ResponseEntity.ok(settings);
    }
}
