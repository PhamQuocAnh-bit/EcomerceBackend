package com.techstore.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageRequest {
    @NotBlank(message = "Url ảnh không được để trống")
    private String imageUrl ;

    private Boolean mainImage;
    private Integer sortOrder;
}
