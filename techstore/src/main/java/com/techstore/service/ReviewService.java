package com.techstore.service;

import com.techstore.dto.reponse.ReviewResponse;
import com.techstore.dto.request.ReviewRequest;
import com.techstore.entity.Product;
import com.techstore.entity.Review;
import com.techstore.entity.User;
import com.techstore.enums.OrderStatus;
import com.techstore.mapper.ReviewMapper;
import com.techstore.repository.OrderRepository;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ReviewRepository;
import com.techstore.repository.UserRepository;
import com.techstore.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ReviewMapper reviewMapper;

    @Transactional
    public ReviewResponse createReview(CustomUserDetails userDetails, Long productId, ReviewRequest request) {
        // Validate if user has purchased the product
        boolean hasPurchased = orderRepository.existsByUserIdAndStatusAndOrderDetailsProductId(
                userDetails.getId(), OrderStatus.DELIVERED, productId
        );

        if (!hasPurchased) {
            throw new RuntimeException("Bạn chỉ có thể đánh giá sản phẩm sau khi đã nhận được hàng (Đơn hàng ở trạng thái DELIVERED).");
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        Review review = Review.builder()
                .user(user)
                .product(product)
                .rating(request.getRating())
                .comment(request.getComment())
                .build();

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public ReviewResponse updateReview(CustomUserDetails userDetails, Long id, ReviewRequest request) {
        Review review = reviewRepository.findByIdAndUserId(id, userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá của bạn hoặc bạn không có quyền sửa đánh giá này"));

        review.setRating(request.getRating());
        review.setComment(request.getComment());

        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Transactional
    public void deleteReview(CustomUserDetails userDetails, Long id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá"));

        boolean isAdmin = userDetails.getRole().equalsIgnoreCase("ADMIN");
        boolean isOwner = review.getUser().getId().equals(userDetails.getId());

        if (!isOwner && !isAdmin) {
            throw new RuntimeException("Bạn không có quyền xóa đánh giá này");
        }

        reviewRepository.delete(review);
    }

    public List<ReviewResponse> getProductReviews(Long productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Không tìm thấy sản phẩm");
        }
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId).stream()
                .map(reviewMapper::toResponse)
                .toList();
    }
}
