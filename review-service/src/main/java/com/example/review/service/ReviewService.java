package com.example.review.service;

import com.example.review.model.Review;
import com.example.review.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;

    public List<Review> getReviews(Long menuId) {
        return reviewRepository.findByMenuIdOrderByCreatedAtDesc(menuId);
    }

    public Review addReview(Long menuId, String content, int rating) {
        Review review = Review.builder()
                .menuId(menuId)
                .content(content)
                .rating(rating)
                .build();
        return reviewRepository.save(review);
    }

    public Review updateReview(Long id, String content, int rating) {
        Optional<Review> optionalReview = reviewRepository.findById(id);
        if (optionalReview.isEmpty()) {
            throw new IllegalArgumentException("리뷰를 찾을 수 없습니다.");
        }

        Review review = optionalReview.get();
        review.setContent(content);
        review.setRating(rating);
        return reviewRepository.save(review);
    }

    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    public long getReviewCount(Long menuId) {
        return reviewRepository.countByMenuId(menuId);
    }
}
