package com.marketlocalshops.sellers;

import com.marketlocalshops.exception.BadRequestException;
import com.marketlocalshops.exception.ResourceNotFoundException;
import com.marketlocalshops.orders.Order;
import com.marketlocalshops.orders.OrderRepository;
import com.marketlocalshops.products.Product;
import com.marketlocalshops.products.ProductRepository;
import com.marketlocalshops.shops.Shop;
import com.marketlocalshops.shops.ShopRepository;
import com.marketlocalshops.users.User;
import com.marketlocalshops.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/seller")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
public class SellerController {

    private final ShopRepository shopRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new BadRequestException("User must be authenticated");
        }
        String principal = auth.getName();
        return userRepository.findByUsername(principal)
                .or(() -> userRepository.findByEmail(principal))
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found for user: " + principal));
    }

    private Shop getSellerShop() {
        User seller = getAuthenticatedUser();
        List<Shop> shops = shopRepository.findByOwner_Id(seller.getId());
        if (shops.isEmpty()) {
            shops = shopRepository.findByOwner_Username(seller.getUsername());
        }
        if (shops.isEmpty()) {
            throw new ResourceNotFoundException("No shop found associated with seller account: " + seller.getUsername());
        }
        return shops.get(0);
    }

    @GetMapping({"/dashboard", "/stats"})
    public ResponseEntity<Map<String, Object>> getSellerDashboard() {
        Shop shop = getSellerShop();
        List<Product> products = productRepository.findByShop_Id(shop.getId());
        List<Order> orders = orderRepository.findByShop_Id(shop.getId());

        long totalProducts = products.size();
        long activeProducts = products.stream().filter(p -> p.getIsApproved() != null && p.getIsApproved()).count();
        long outOfStock = products.stream().filter(p -> p.getStock() == null || p.getStock() <= 0).count();

        long totalOrders = orders.size();
        long pendingOrders = orders.stream().filter(o -> "PENDING".equalsIgnoreCase(o.getStatus())).count();

        double revenue = orders.stream()
                .mapToDouble(o -> o.getTotalAmount() != null ? o.getTotalAmount() : 0.0)
                .sum();

        long views = products.stream().mapToLong(p -> p.getViewCount() != null ? p.getViewCount() : 0).sum();

        List<Map<String, Object>> topProducts = products.stream()
                .limit(5)
                .map(p -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", p.getId());
                    map.put("name", p.getName());
                    map.put("price", p.getPrice());
                    map.put("stock", p.getStock());
                    map.put("views", p.getViewCount() != null ? p.getViewCount() : 0);
                    return map;
                }).collect(Collectors.toList());

        List<Order> recentOrders = orders.stream().limit(5).collect(Collectors.toList());

        Map<String, Object> stats = new HashMap<>();
        stats.put("shopId", shop.getId());
        stats.put("shopName", shop.getName());
        stats.put("totalProducts", totalProducts);
        stats.put("activeProducts", activeProducts);
        stats.put("outOfStock", outOfStock);
        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("revenue", revenue);
        stats.put("monthlyRevenue", revenue * 0.4);
        stats.put("views", views);
        stats.put("topProducts", topProducts);
        stats.put("recentOrders", recentOrders);

        return ResponseEntity.ok(stats);
    }

    @GetMapping("/shop")
    public ResponseEntity<Shop> getSellerShopDetails() {
        return ResponseEntity.ok(getSellerShop());
    }

    @PutMapping("/shop")
    public ResponseEntity<Shop> updateSellerShop(@RequestBody Shop updates) {
        Shop shop = getSellerShop();
        if (updates.getName() != null) shop.setName(updates.getName());
        if (updates.getDescription() != null) shop.setDescription(updates.getDescription());
        if (updates.getPhone() != null) shop.setPhone(updates.getPhone());
        if (updates.getAddress() != null) shop.setAddress(updates.getAddress());
        if (updates.getImageUrl() != null) shop.setImageUrl(updates.getImageUrl());
        return ResponseEntity.ok(shopRepository.save(shop));
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getSellerProducts() {
        Shop shop = getSellerShop();
        return ResponseEntity.ok(productRepository.findByShop_Id(shop.getId()));
    }

    @PostMapping("/products")
    public ResponseEntity<Product> createSellerProduct(@RequestBody Product product) {
        Shop shop = getSellerShop();
        product.setShop(shop);
        if (product.getStock() == null) product.setStock(10);
        if (product.getIsApproved() == null) product.setIsApproved(true);
        return ResponseEntity.status(HttpStatus.CREATED).body(productRepository.save(product));
    }

    @PutMapping("/products/{id}")
    public ResponseEntity<Product> updateSellerProduct(@PathVariable Long id, @RequestBody Product updates) {
        Shop shop = getSellerShop();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (product.getShop() == null || !product.getShop().getId().equals(shop.getId())) {
            throw new BadRequestException("Unauthorized attempt to update a product belonging to another shop");
        }
        if (updates.getName() != null) product.setName(updates.getName());
        if (updates.getPrice() != null) product.setPrice(updates.getPrice());
        if (updates.getStock() != null) product.setStock(updates.getStock());
        if (updates.getDescription() != null) product.setDescription(updates.getDescription());
        if (updates.getCategory() != null) product.setCategory(updates.getCategory());
        if (updates.getImageUrl() != null) product.setImageUrl(updates.getImageUrl());

        return ResponseEntity.ok(productRepository.save(product));
    }

    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteSellerProduct(@PathVariable Long id) {
        Shop shop = getSellerShop();
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        if (product.getShop() == null || !product.getShop().getId().equals(shop.getId())) {
            throw new BadRequestException("Unauthorized attempt to delete a product belonging to another shop");
        }
        productRepository.delete(product);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getSellerOrders() {
        Shop shop = getSellerShop();
        return ResponseEntity.ok(orderRepository.findByShop_Id(shop.getId()));
    }

    @RequestMapping(value = "/orders/{id}/status", method = {RequestMethod.PUT, RequestMethod.PATCH})
    public ResponseEntity<Order> updateSellerOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> body) {
        Shop shop = getSellerShop();
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        
        boolean containsShopProduct = order.getItems() != null && order.getItems().stream()
                .anyMatch(i -> i.getProduct() != null && i.getProduct().getShop() != null && i.getProduct().getShop().getId().equals(shop.getId()));
        
        if (!containsShopProduct) {
            throw new BadRequestException("Unauthorized attempt to update order status for another seller");
        }

        if (body.containsKey("status")) {
            order.setStatus(body.get("status").toUpperCase());
        }
        return ResponseEntity.ok(orderRepository.save(order));
    }

    @GetMapping({"/analytics", "/reports"})
    public ResponseEntity<Map<String, Object>> getSellerAnalytics() {
        return getSellerDashboard();
    }
}
