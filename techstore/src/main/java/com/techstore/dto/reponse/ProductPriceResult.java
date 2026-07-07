package com.techstore.dto.reponse;

import com.techstore.enums.DiscountType;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductPriceResult {
    private BigDecimal originalPrice;
    private BigDecimal salePrice ;
    private BigDecimal finalPrice ;
    private Boolean onSale ;
    private BigDecimal discountAmount;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private String saleName;
}
