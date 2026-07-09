package com.techstore.dto.reponse;


import com.techstore.enums.DiscountType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderDetailResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productImage;

    private BigDecimal originalPrice;
    private BigDecimal salePrice;
    private BigDecimal finalPrice;

    private Boolean onSale;
    private BigDecimal discountAmount;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private String saleName;

    private Integer quantity;
    private BigDecimal totalPrice;
}
