package com.techstore.service;

import com.techstore.dto.reponse.ProductPriceResult;
import com.techstore.dto.reponse.WishlistResponse;
import com.techstore.entity.Product;
import com.techstore.entity.User;
import com.techstore.entity.Wishlist;
import com.techstore.mapper.WishlistMapper;
import com.techstore.repository.ProductRepository;
import com.techstore.repository.ProductSaleRepository;
import com.techstore.repository.UserRepository;
import com.techstore.repository.WishlistRepository;
import com.techstore.security.CustomUserDetails;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductPricingService productPricingService;
    private final ProductSaleRepository productSaleRepository;
    private final WishlistMapper wishlistMapper;

    @Transactional
    public WishlistResponse addToWishlist(CustomUserDetails userDetails, Long productId) {
        if (wishlistRepository.existsByUserIdAndProductId(userDetails.getId(), productId)) {
            throw new RuntimeException("Sản phẩm đã tồn tại trong danh sách yêu thích");
        }

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin tài khoản"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .product(product)
                .build();

        Wishlist saved = wishlistRepository.save(wishlist);

        ProductPriceResult price = productPricingService.calculatePrice(
                product,
                productSaleRepository.findActiveSalesByProductId(product.getId())
        );

        return wishlistMapper.toResponse(saved, price);
    }

    @Transactional
    public void removeFromWishlist(CustomUserDetails userDetails, Long productId) {
        Wishlist wishlist = wishlistRepository.findByUserIdAndProductId(userDetails.getId(), productId)
                .orElseThrow(() -> new RuntimeException("Sản phẩm không có trong danh sách yêu thích"));
        wishlistRepository.delete(wishlist);
    }

    public List<WishlistResponse> getMyWishlist(CustomUserDetails userDetails) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userDetails.getId()).stream()
                .map(wishlist -> {
                    Product product = wishlist.getProduct();
                    ProductPriceResult price = productPricingService.calculatePrice(
                            product,
                            productSaleRepository.findActiveSalesByProductId(product.getId())
                    );
                    return wishlistMapper.toResponse(wishlist, price);
                })
                .toList();
    }
}
