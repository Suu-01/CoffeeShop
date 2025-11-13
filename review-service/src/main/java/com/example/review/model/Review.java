package com.example.review.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 메뉴아이디 가져오기
    private Long menuId;

    // 리뷰 카운트
    @Column(length = 1000)
    private String content;

    private int rating; // ⭐ 별점 (1~5)

    // 리뷰 작성 실시간 시간 표시
    @CreationTimestamp
    private LocalDateTime createdAt;
}
