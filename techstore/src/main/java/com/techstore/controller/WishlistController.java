package com.techstore.controller;

import com.techstore.dto.reponse.WishlistResponse;
import com.techstore.security.CustomUserDetails;
import com.techstore.service.WishlistService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/wishlist")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class WishlistController {

    private final WishlistService wishlistService;

    @GetMapping
    public List<WishlistResponse> getMyWishlist(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return wishlistService.getMyWishlist(userDetails);
    }

    @PostMapping("/add/{productId}")
    public WishlistResponse addToWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId
    ) {
        return wishlistService.addToWishlist(userDetails, productId);
    }

    @DeleteMapping("/remove/{productId}")
    public String removeFromWishlist(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long productId
    ) {
        wishlistService.removeFromWishlist(userDetails, productId);
        return "Đã xóa sản phẩm khỏi danh sách yêu thích";
    }
}
