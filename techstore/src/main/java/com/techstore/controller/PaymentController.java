package com.techstore.controller;

import com.techstore.dto.reponse.PaymentResponse;
import com.techstore.dto.reponse.VNPayCallbackResponse;
import com.techstore.dto.reponse.VNPayIpnResponse;
import com.techstore.security.CustomUserDetails;
import com.techstore.service.PaymentService;
import com.techstore.service.VNPayService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final VNPayService vnPayService;

    @GetMapping("/orders/{orderId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public PaymentResponse getPaymentByOrderId(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long orderId
    ) {
        return paymentService.getPaymentByOrderId(
                user,
                orderId
        );
    }

    @GetMapping("/{paymentId}")
    @SecurityRequirement(name = "Bearer Authentication")
    public PaymentResponse getPayment(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long paymentId
    ) {
        return paymentService.getPayment(
                user,
                paymentId
        );
    }

    @PostMapping("/orders/{orderId}/payment-url")
    @SecurityRequirement(name = "Bearer Authentication")
    public PaymentResponse createVNPayUrl(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long orderId,
            HttpServletRequest request
    ) {
        return vnPayService.createPaymentUrl(
                user,
                orderId,
                request
        );
    }

    @GetMapping("/vnpay/return")
    public VNPayCallbackResponse vnPayReturn(
            HttpServletRequest request
    ) {
        return vnPayService.processReturn(request);
    }

    @GetMapping("/vnpay/ipn")
    public VNPayIpnResponse vnPayIpn(
            HttpServletRequest request
    ) {
        return vnPayService.processIpn(request);
    }

    @GetMapping("/admin")
    @SecurityRequirement(name = "Bearer Authentication")
    public List<PaymentResponse> getAllPayments() {
        return paymentService.getAllPayments();
    }
}