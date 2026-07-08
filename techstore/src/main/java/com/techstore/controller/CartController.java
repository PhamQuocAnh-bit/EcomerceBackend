package com.techstore.controller;


import com.techstore.dto.reponse.CartResponse;
import com.techstore.dto.request.AddToCartRequest;
import com.techstore.dto.request.UpdateCartItemRequest;
import com.techstore.security.CustomUserDetails;
import com.techstore.service.CartService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class CartController {
    private final CartService cartService;

    @GetMapping
    public CartResponse getMyCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return cartService.getMyCart(userDetails);
    }

    @PostMapping("/items")
    public CartResponse addToCart(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody AddToCartRequest request
    ) {
        return cartService.addToCart(userDetails, request);
    }

    @PutMapping("/items/{itemId}")
    public CartResponse updateItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId,
            @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItem(userDetails, itemId, request);
    }

    @DeleteMapping("/items/{itemId}")
    public CartResponse removeItem(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long itemId
    ) {
        return cartService.removeItem(userDetails, itemId);
    }

    @DeleteMapping
    public CartResponse clearCart(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return cartService.clearCart(userDetails);
    }
}
