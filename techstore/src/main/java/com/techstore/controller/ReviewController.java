package com.techstore.controller;

import com.techstore.dto.reponse.ReviewResponse;
import com.techstore.dto.request.ReviewRequest;
import com.techstore.security.CustomUserDetails;
import com.techstore.service.ReviewService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;

    @GetMapping("/products/{productId}/reviews")
    public List<ReviewResponse> getProductReviews(@PathVariable Long productId) {
        return reviewService.getProductReviews(productId);
    }

    @PostMapping("/products/{productId}/reviews")
    @SecurityRequirement(name = "Bearer Authentication")
    public ReviewResponse createReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.createReview(userDetails, productId, request);
    }

    @PutMapping("/reviews/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public ReviewResponse updateReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody ReviewRequest request
    ) {
        return reviewService.updateReview(userDetails, id, request);
    }

    @DeleteMapping("/reviews/{id}")
    @SecurityRequirement(name = "Bearer Authentication")
    public String deleteReview(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        reviewService.deleteReview(userDetails, id);
        return "Xóa đánh giá thành công";
    }
}
