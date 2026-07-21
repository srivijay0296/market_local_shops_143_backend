package com.marketlocalshops.orders;

import com.marketlocalshops.exception.ResourceNotFoundException;
import com.marketlocalshops.users.User;
import com.marketlocalshops.users.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final com.marketlocalshops.products.ProductRepository productRepository;
    private final OrderMapper orderMapper;

    @Transactional(readOnly = true)
    public Page<OrderDTO> findOrdersWithFilters(Long userId, String status, String search, Pageable pageable) {
        return orderRepository.findOrdersWithFilters(userId, status, search, pageable)
                .map(orderMapper::toDto);
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findByUserId(Long userId) {
        return orderRepository.findByUser_Id(userId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findByShopId(Long shopId) {
        return orderRepository.findByShop_Id(shopId).stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        
        Long userId = order.getUserId();
        if (userId == null && orderDTO.getUser() != null) {
            userId = orderDTO.getUser().getId();
        }

        if (userId == null) {
            try {
                org.springframework.security.core.Authentication auth = 
                        org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getPrincipal())) {
                    String username = auth.getName();
                    User currentUser = userRepository.findByUsername(username)
                            .or(() -> userRepository.findByEmail(username)).orElse(null);
                    if (currentUser != null) {
                        order.setUser(currentUser);
                        order.setUserId(currentUser.getId());
                    }
                }
            } catch (Exception ignored) {}
        } else {
            Long targetUserId = userId;
            User user = userRepository.findById(targetUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + targetUserId));
            order.setUser(user);
            order.setUserId(user.getId());
        }

        double calculatedTotal = 0.0;
        if (order.getItems() != null && !order.getItems().isEmpty()) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
                if (item.getProduct() != null && item.getProduct().getId() != null) {
                    com.marketlocalshops.products.Product product = productRepository.findById(item.getProduct().getId())
                            .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + item.getProduct().getId()));
                    
                    if (product.getStock() != null && product.getStock() < item.getQuantity()) {
                        throw new com.marketlocalshops.exception.BadRequestException("Insufficient stock for product '" + product.getName() + "'. Available: " + product.getStock());
                    }
                    
                    // Deduct stock
                    if (product.getStock() != null) {
                        product.setStock(product.getStock() - item.getQuantity());
                        productRepository.save(product);
                    }
                    
                    item.setProduct(product);
                    if (item.getPrice() == null) {
                        item.setPrice(product.getPrice());
                    }
                }
                double itemPrice = item.getPrice() != null ? item.getPrice() : 0.0;
                int itemQty = item.getQuantity() != null ? item.getQuantity() : 1;
                calculatedTotal += (itemPrice * itemQty);
            }
        }
        
        if (order.getTotalAmount() == null || order.getTotalAmount() <= 0) {
            order.setTotalAmount(calculatedTotal);
        }
        if (order.getStatus() == null || order.getStatus().isBlank()) {
            order.setStatus("PENDING");
        }
        
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, Map<String, String> updates) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
                
        if (updates.containsKey("status")) {
            String newStatus = updates.get("status").toUpperCase();
            String oldStatus = order.getStatus() != null ? order.getStatus().toUpperCase() : "";
            
            // Restore stock if cancelled
            if ("CANCELLED".equals(newStatus) && !"CANCELLED".equals(oldStatus)) {
                if (order.getItems() != null) {
                    for (OrderItem item : order.getItems()) {
                        if (item.getProduct() != null && item.getProduct().getId() != null) {
                            com.marketlocalshops.products.Product product = productRepository.findById(item.getProduct().getId()).orElse(null);
                            if (product != null && product.getStock() != null) {
                                product.setStock(product.getStock() + item.getQuantity());
                                productRepository.save(product);
                            }
                        }
                    }
                }
            }
            order.setStatus(newStatus);
        }
        
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Transactional
    public OrderDTO updateOrder(Long id, OrderDTO updates) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        if (updates.getStatus() != null) {
            Map<String, String> statusMap = new java.util.HashMap<>();
            statusMap.put("status", updates.getStatus());
            return updateOrderStatus(id, statusMap);
        }
        if (updates.getShippingAddress() != null) order.setShippingAddress(updates.getShippingAddress());
        if (updates.getCustomerName() != null) order.setCustomerName(updates.getCustomerName());
        if (updates.getCustomerPhone() != null) order.setCustomerPhone(updates.getCustomerPhone());
        if (updates.getCustomerEmail() != null) order.setCustomerEmail(updates.getCustomerEmail());
        
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Transactional
    public void deleteOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        orderRepository.delete(order);
    }
}
