package com.example.review.controller;

import com.example.review.model.Review;
import com.example.review.repository.ReviewRepository;
import com.example.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewRepository reviewRepository;

    @GetMapping("/menu/{menuId}")
    public List<Review> getReviews(@PathVariable Long menuId) {
        return reviewService.getReviews(menuId);
    }


    // 리뷰 등록
    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review.getMenuId(), review.getContent(), review.getRating());
    }


    // 리뷰 수정
    @PutMapping("/{id}")
    public Review updateReview(@PathVariable Long id, @RequestBody Review review) {
        return reviewService.updateReview(id, review.getContent(), review.getRating());
    }

    // 리뷰 삭제
    @DeleteMapping("/{id}")
    public void deleteReview(@PathVariable Long id) {
        reviewService.deleteReview(id);
    }

    // 메뉴별 리뷰 개수 반환
    @GetMapping("/count/{menuId}")
    public long getCount(@PathVariable Long menuId) {
        return reviewService.getReviewCount(menuId);
    }

    // 메뉴별 평균 별점 반환
    @GetMapping("/avg/{menuId}")
    public double getAverageRating(@PathVariable Long menuId) {
        return reviewRepository.findByMenuId(menuId)
                .stream()
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }
}
