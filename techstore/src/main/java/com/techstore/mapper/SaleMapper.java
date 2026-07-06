package com.techstore.mapper;

import com.techstore.dto.reponse.SaleResponse;
import com.techstore.entity.ProductSale;
import com.techstore.entity.Sale;
import org.springframework.stereotype.Component;

import java.util.List;



@Component
public class SaleMapper {
    public SaleResponse toResponse(Sale sale, List<ProductSale> productSales) {
        List<Long> productIds = productSales.stream()
                .map(ps -> ps.getProduct().getId())
                .toList();
        return SaleResponse.builder()
                .id(sale.getId())
                .name(sale.getName())
                .description(sale.getDescription())
                .discountType(sale.getDiscountType())
                .discountValue(sale.getDiscountValue())
                .startDate(sale.getStartDate())
                .endDate(sale.getEndDate())
                .status(sale.getStatus())
                .productId(productIds)
                .createdAt(sale.getCreatedAt())
                .updatedAt(sale.getUpdatedAt())
                .build();

    }
}
