package com.techstore.dto.reponse;


import com.techstore.enums.DiscountType;
import com.techstore.enums.ProductStatus;

import java.time.LocalDateTime;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductResponse {
    private Long id ;
    private String name ;
    private String sku ;
    private String slug;
    private String description ;
    private BigDecimal originalPrice ;
    private BigDecimal salePrice;
    private BigDecimal finalPrice;
    private Boolean onSale;
    private BigDecimal discountAmount;
    private DiscountType discountType;
    private BigDecimal discountValue;
    private String saleName ;
    private Integer stockQuantity;
    private Integer soldQuantity;
    private ProductStatus status ;
    private Long categoryId ;
    private String categoryName ;
    private Long brandId ;
    private String brandName ;
    private String mainImage;
    private List<ProductImageResponse> images;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt ;


}
