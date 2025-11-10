package com.example.product.controller;

import com.example.product.model.Order;
import com.example.product.model.OrderItem;
import com.example.product.repository.OrderRepository;
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

    // 모든 주문 목록을 조회하는 API
    @GetMapping
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 특정 주문 ID로 주문을 조회하는 API
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 새로운 주문을 생성하는 API
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                item.setOrder(order);
            }
            order.recalculateTotal();
        }
        Order saved = orderRepository.save(order);
        return ResponseEntity.ok(saved);
    }

    // 기존 주문을 전체 수정(덮어쓰기)하는 API
    @PutMapping("/{id}")
    public ResponseEntity<Order> updateOrder(@PathVariable Long id, @RequestBody Order incoming) {
        return orderRepository.findById(id)
                .map(existing -> {
                    existing.getItems().clear();
                    if (incoming.getItems() != null) {
                        for (OrderItem item : incoming.getItems()) {
                            item.setOrder(existing);
                            existing.getItems().add(item);
                        }
                    }
                    existing.setCustomerUsername(incoming.getCustomerUsername());
                    existing.setStatus(incoming.getStatus());
                    existing.recalculateTotal();
                    return ResponseEntity.ok(orderRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 주문의 상태만 부분 수정하는 API
    @PatchMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(@PathVariable Long id, @RequestBody StatusPatch patch) {
        return orderRepository.findById(id)
                .map(existing -> {
                    existing.setStatus(patch.status);
                    return ResponseEntity.ok(orderRepository.save(existing));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 특정 주문을 삭제하는 API
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        return orderRepository.findById(id)
                .map(existing -> {
                    orderRepository.delete(existing);
                    return ResponseEntity.ok().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // PATCH 요청 본문에서 status 값을 받기 위한 DTO(간단한 내부 클래스)
    public static class StatusPatch {
        public String status;
    }
}
