package com.techstore.dto.reponse;


import com.techstore.enums.DiscountType;
import com.techstore.enums.SaleStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class SaleResponse {
    private Long id ;
    private String name ;
    private String description;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private LocalDateTime startDate ;
    private LocalDateTime endDate ;
    private SaleStatus status ;
    private List<Long> productId;
    private LocalDateTime createdAt ;
    private LocalDateTime updatedAt ;
}
