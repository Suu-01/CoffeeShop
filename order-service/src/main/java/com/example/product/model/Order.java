package com.example.product.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ORDERS")
@Getter
@Setter
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 로그인한 사용자명 (주문자 표시용)
    @Column(nullable = false)
    private String customerUsername;

    // 주문 총액 (항목 합계)
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice = BigDecimal.ZERO;

    // 주문 상태 (예: CREATED, PAID, CANCELED)
    @Column(nullable = false, length = 20)
    private String status = "CREATED";

    // 생성 시각
    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    // 주문 항목들
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<OrderItem> items = new ArrayList<>();

    public void addItem(OrderItem item) {
        item.setOrder(this);
        this.items.add(item);
        recalculateTotal();
    }

    public void removeItem(OrderItem item) {
        this.items.remove(item);
        item.setOrder(null);
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalPrice = this.items.stream()
                .map(OrderItem::getLineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}