package com.example.menu.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "cafe_menus")
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 메뉴 이름
    private String category;    // 메뉴 종류 (커피, 디저트 등)
    private String description; // 메뉴 설명
    
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal price;  // 가격 (원) - OrderItem.unitPrice와 타입 일치
}
