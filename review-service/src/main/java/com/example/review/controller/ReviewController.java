package com.example.review.controller;

import com.example.review.model.Review;
import com.example.review.repository.ReviewRepository;
import com.example.review.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Base64;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestParam;

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
                .filter(r -> r.getContent() != null && !r.getContent().trim().isEmpty())  // 이미지용 제외
                .mapToInt(Review::getRating)
                .average()
                .orElse(0.0);
    }

    // ✅ 추가: 메뉴 이미지 조회 (상세페이지용)
    @GetMapping("/menu/{menuId}/image")
    public ResponseEntity<Review> getMenuImage(@PathVariable Long menuId) {
        return reviewRepository.findByMenuIdAndImageDataNotNull(menuId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ✅ 추가: 리뷰에 이미지 업로드
    @PostMapping("/{id}/image")
    public ResponseEntity<Review> uploadReviewImage(@PathVariable Long id, 
                                                   @RequestParam("file") MultipartFile file) {
        try {
            byte[] fileBytes = file.getBytes();
            String base64Image = Base64.getEncoder().encodeToString(fileBytes);
            String imageData = "data:" + file.getContentType() + ";base64," + base64Image;
            
            return reviewRepository.findById(id)
                    .map(review -> {
                        review.setImageData(imageData);
                        return ResponseEntity.ok(reviewRepository.save(review));
                    })
                    .orElse(ResponseEntity.notFound().build());
        } catch (IOException e) {
            return ResponseEntity.status(500).build();
        }
    }

    // ✅ 추가: 리뷰의 이미지 삭제
    @DeleteMapping("/{id}/image")
    public ResponseEntity<Review> deleteReviewImage(@PathVariable Long id) {
        return reviewRepository.findById(id)
                .map(review -> {
                    review.setImageData(null);
                    return ResponseEntity.ok(reviewRepository.save(review));
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
