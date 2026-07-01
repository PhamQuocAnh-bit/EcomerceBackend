package com.techstore.mapper;

import com.techstore.dto.reponse.BrandResponse;
import com.techstore.dto.request.BrandRequest;
import com.techstore.entity.Brand;
import org.springframework.stereotype.Component;

@Component
public class BrandMapper {
    public Brand toBrand(BrandRequest request) {
        return Brand.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();
    }
    public BrandResponse toResponse(Brand brand){
        return BrandResponse.builder()
                .id(brand.getId())
                .name(brand.getName())
                .description(brand.getDescription())
                .status(brand.getStatus())
                .createdAt(brand.getCreatedAt())
                .updateAt(brand.getUpdatedAt())
                .build();
    }
}
