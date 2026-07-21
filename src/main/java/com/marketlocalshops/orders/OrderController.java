package com.marketlocalshops.orders;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<Object> getAllOrders(
            @RequestParam(name = "user_id", required = false) Long userId,
            @RequestParam(name = "status", required = false) String status,
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size,
            @RequestParam(name = "sort", defaultValue = "id,desc") String sort) {
        
        int pageNum = (page != null && page >= 0) ? page : 0;
        int pageSize = (size != null && size > 0) ? size : 20;
        
        String[] sortParts = (sort != null && !sort.isBlank()) ? sort.split(",") : new String[]{"id", "desc"};
        Sort.Direction direction = (sortParts.length > 1 && "asc".equalsIgnoreCase(sortParts[1])) 
                ? Sort.Direction.ASC 
                : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(pageNum, pageSize, Sort.by(direction, sortParts[0]));

        if (page == null && size == null) {
            Page<OrderDTO> orderPage = orderService.findOrdersWithFilters(userId, status, search, pageable);
            return ResponseEntity.ok(orderPage.getContent());
        }

        return ResponseEntity.ok(orderService.findOrdersWithFilters(userId, status, search, pageable));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<OrderDTO> createOrder(@jakarta.validation.Valid @RequestBody OrderDTO orderDTO) {
        return ResponseEntity.status(org.springframework.http.HttpStatus.CREATED).body(orderService.createOrder(orderDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<OrderDTO> updateOrder(@PathVariable Long id, @jakarta.validation.Valid @RequestBody OrderDTO updates) {
        return ResponseEntity.ok(orderService.updateOrder(id, updates));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<OrderDTO> updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> updates) {
        return ResponseEntity.ok(orderService.updateOrderStatus(id, updates));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'SELLER', 'ADMIN')")
    public ResponseEntity<List<OrderDTO>> getOrdersByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.findByUserId(userId));
    }

    @GetMapping("/shop/{shopId}")
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<List<OrderDTO>> getOrdersByShop(@PathVariable Long shopId) {
        return ResponseEntity.ok(orderService.findByShopId(shopId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}
