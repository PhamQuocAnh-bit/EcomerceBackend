package com.techstore.mapper;

import com.techstore.dto.reponse.PaymentResponse;
import com.techstore.entity.Payment;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return PaymentResponse.builder()
                .id(payment.getId())
                .paymentCode(payment.getPaymentCode())

                .orderId(payment.getOrder().getId())
                .orderCode(payment.getOrder().getOrderCode())

                .paymentMethod(payment.getPaymentMethod())
                .status(payment.getStatus())
                .amount(payment.getAmount())

                .transactionCode(payment.getTransactionCode())
                .bankCode(payment.getBankCode())
                .cardType(payment.getCardType())

                .responseCode(payment.getResponseCode())
                .transactionStatus(payment.getTransactionStatus())

                .paymentUrl(payment.getPaymentUrl())
                .failureReason(payment.getFailureReason())

                .paidAt(payment.getPaidAt())
                .expiredAt(payment.getExpiredAt())
                .createdAt(payment.getCreatedAt())
                .updatedAt(payment.getUpdatedAt())
                .build();
    }
}