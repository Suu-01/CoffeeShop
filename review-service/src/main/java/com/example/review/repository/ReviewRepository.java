package com.example.review.repository;

import com.example.review.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMenuIdOrderByCreatedAtDesc(Long menuId);
    long countByMenuId(Long menuId);
    List<Review> findByMenuId(Long menuId);
    
    // 추가: 메뉴 이미지 조회
    Optional<Review> findByMenuIdAndImageDataNotNull(Long menuId);
}
