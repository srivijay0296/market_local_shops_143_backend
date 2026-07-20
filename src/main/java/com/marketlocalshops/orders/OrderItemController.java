package com.marketlocalshops.orders;

import com.marketlocalshops.products.Product;
import com.marketlocalshops.products.ProductRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/order_items")
@RequiredArgsConstructor
public class OrderItemController {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    @PostMapping
    public ResponseEntity<List<OrderItem>> createOrderItems(@RequestBody OrderItemRequest request) {
        List<OrderItem> savedItems = new ArrayList<>();
        if (request.getItems() != null) {
            for (ItemDTO dto : request.getItems()) {
                Order order = orderRepository.findById(dto.getOrder_id()).orElse(null);
                Product product = productRepository.findById(dto.getProduct_id()).orElse(null);
                if (order != null && product != null) {
                    OrderItem item = OrderItem.builder()
                            .order(order)
                            .product(product)
                            .quantity(dto.getQuantity())
                            .price(dto.getPrice_at_time())
                            .build();
                    savedItems.add(orderItemRepository.save(item));
                }
            }
        }
        return ResponseEntity.ok(savedItems);
    }

    @Data
    public static class OrderItemRequest {
        private List<ItemDTO> items;
    }

    @Data
    public static class ItemDTO {
        private Long order_id;
        private Long product_id;
        private Long shop_id;
        private Integer quantity;
        private Double price_at_time;
    }
}
