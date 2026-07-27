package com.techstore.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private String productSlug;
    private BigDecimal originalPrice;
    private BigDecimal finalPrice;
    private String mainImage;
    private LocalDateTime createdAt;
}
