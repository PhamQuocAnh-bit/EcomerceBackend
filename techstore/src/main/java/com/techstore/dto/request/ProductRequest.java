package com.techstore.dto.request;


import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ProductRequest {
    @NotBlank(message = "Sku không được để trống")
    private  String sku ;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    private String name ;

    private String description ;

    @NotNull(message = "Giá gốc không được để trống")
    @DecimalMin(value = "0.0", inclusive = false,message = "Gia phải lon hon 0")
    private BigDecimal originalPrice ;

    @NotNull(message = "Số lượng tồn kho không được để trống")
    @Min(value = 0 , message = "Số luong tồn kho không đươợc âm")
    private Integer stockQuantity;

    @NotNull(message = "Danh mục không được để trống")
    private Long categoryId;

    @NotNull(message = "Thương hiệu không được để trống")
    private Long brandId;


    private List<ProductImageRequest> images;

}
