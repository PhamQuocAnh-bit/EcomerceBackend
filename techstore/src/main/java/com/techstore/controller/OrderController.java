package com.techstore.controller;


import com.techstore.dto.reponse.OrderResponse;
import com.techstore.dto.request.CreateOrderRequest;
import com.techstore.security.CustomUserDetails;
import com.techstore.service.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@SecurityRequirement(name = "Bearer Authentication")
public class OrderController {
    private final OrderService orderService;

    @PostMapping
    public OrderResponse createOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @Valid @RequestBody CreateOrderRequest request
    ) {
        return orderService.createOrder(userDetails, request);
    }

    @GetMapping
    public List<OrderResponse> getMyOrders(
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        return orderService.getMyOrders(userDetails);
    }

    @GetMapping("/{id}")
    public OrderResponse getMyOrderById(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        return orderService.getMyOrderById(userDetails, id);
    }

    @PutMapping("/{id}/cancel")
    public OrderResponse cancelMyOrder(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long id
    ) {
        return orderService.cancelMyOrder(userDetails, id);
    }
}
