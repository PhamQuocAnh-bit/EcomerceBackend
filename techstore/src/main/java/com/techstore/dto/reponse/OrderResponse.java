package com.techstore.dto.reponse;


import com.techstore.enums.OrderStatus;
import com.techstore.enums.PaymentMethod;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class OrderResponse {
    private Long id;
    private String orderCode;

    private String receiverName;
    private String receiverPhone;
    private String province;
    private String district;
    private String ward;
    private String addressDetail;

    private BigDecimal totalProductAmount;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;

    private PaymentMethod paymentMethod;
    private OrderStatus status;
    private String note;

    private List<OrderDetailResponse> items;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
