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
    public List<OrderDTO> findAll() {
        return orderRepository.findAll().stream()
                .map(orderMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public OrderDTO getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
        return orderMapper.toDto(order);
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        Order order = orderMapper.toEntity(orderDTO);
        
        if (order.getUserId() != null) {
            User user = userRepository.findById(order.getUserId()).orElse(null);
            order.setUser(user);
        }
        
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
            }
        }
        
        Order saved = orderRepository.save(order);
        return orderMapper.toDto(saved);
    }

    @Transactional
    public OrderDTO updateOrderStatus(Long id, Map<String, String> updates) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
                
        if (updates.containsKey("status")) {
            order.setStatus(updates.get("status"));
        }
        
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
