package com.example.product.controller;

import com.example.product.model.Order;
import com.example.product.model.OrderItem;
import com.example.product.repository.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderRepository orderRepository;

    public OrderController(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
                // lineTotal 명시적으로 계산
                if (item.getUnitPrice() != null && item.getQuantity() != null) {
                    item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                }
            }
            order.recalculateTotal();
        }
        Order saved = orderRepository.save(order);
        // 저장 후 다시 한 번 총액 계산 (PrePersist/PreUpdate 트리거 확인)
        saved.recalculateTotal();
        return ResponseEntity.ok(orderRepository.save(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order incoming) {
        return orderRepository.findById(id)
                .map(existing -> {
                    existing.getItems().clear();
                    if (incoming.getItems() != null) {
                        for (OrderItem item : incoming.getItems()) {
                            item.setOrder(existing);
                            // lineTotal 명시적으로 계산
                            if (item.getUnitPrice() != null && item.getQuantity() != null) {
                                item.setLineTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                            }
                            existing.getItems().add(item);
                        }
                    }
                    existing.setCustomerUsername(incoming.getCustomerUsername());
                    existing.setStatus(incoming.getStatus());
                    existing.recalculateTotal();
                    Order saved = orderRepository.save(existing);
                    // 저장 후 다시 한 번 총액 계산
                    saved.recalculateTotal();
                    return ResponseEntity.ok(orderRepository.save(saved));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestBody StatusPatch patch) {
        return orderRepository.findById(id)
                .map(existing -> {
                    existing.setStatus(patch.status);
                    return ResponseEntity.ok(orderRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(existing -> {
                    orderRepository.delete(existing);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    public static class StatusPatch {
        public String status;
    }
}