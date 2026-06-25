package com.marketlocalshops.orders;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        // In a real scenario, map DTOs and publish an OrderCreatedEvent to RabbitMQ
        return ResponseEntity.ok(orderRepository.save(order));
    }
}
