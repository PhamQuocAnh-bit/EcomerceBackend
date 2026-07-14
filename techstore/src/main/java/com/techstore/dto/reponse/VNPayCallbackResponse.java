package com.techstore.dto.reponse;

import com.techstore.enums.PaymentStatus;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VNPayCallbackResponse {

    private boolean success;
    private String message;

    private Long paymentId;
    private Long orderId;

    private String paymentCode;
    private String orderCode;

    private PaymentStatus paymentStatus;

    private String transactionCode;
    private String responseCode;
    private String transactionStatus;
}