package com.techstore.dto.reponse;

import com.techstore.enums.PaymentMethod;
import com.techstore.enums.PaymentStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {

    private Long id;
    private String paymentCode;

    private Long orderId;
    private String orderCode;

    private PaymentMethod paymentMethod;
    private PaymentStatus status;

    private BigDecimal amount;

    private String transactionCode;
    private String bankCode;
    private String cardType;

    private String responseCode;
    private String transactionStatus;

    private String paymentUrl;
    private String failureReason;

    private LocalDateTime paidAt;
    private LocalDateTime expiredAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}