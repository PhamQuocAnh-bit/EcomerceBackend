package com.techstore.dto.request;


import com.techstore.enums.DiscountType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SaleRequest {
    @NotBlank(message = "Tên chương trình không được để trong")
    private String name ;

    private  String description;
    @NotNull(message = "Loại giảm giá này không được để trống")
    private DiscountType discountType;

    @NotNull(message = "Giá trị giảm giá khong được để trống ")
    @DecimalMin(value = "0.0", inclusive = false, message = "Giá trị giảm giá phải lớn hơn 0")
    private BigDecimal discountValue;


    @NotNull(message = "Ngày bắt đầu không được để trống")
    private LocalDateTime startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    private LocalDateTime endDate;

    private List<Long> productIds;

}
