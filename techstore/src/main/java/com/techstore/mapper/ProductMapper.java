package com.techstore.mapper;

import com.techstore.dto.reponse.ProductImageResponse;
import com.techstore.dto.reponse.ProductPriceResult;
import com.techstore.dto.reponse.ProductResponse;
import com.techstore.entity.Product;
import com.techstore.entity.ProductImage;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;

@Component
public class ProductMapper {
    public ProductResponse toResponse (Product product, ProductPriceResult price){
        List<ProductImageResponse> imageResponses = toImageResponses(product.getImages());
        String mainImage = imageResponses.stream()
                .filter(img -> Boolean.TRUE.equals(img.getMainImage()))
                .findFirst()
                .map(ProductImageResponse::getImageUrl)
                .orElse(null);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .sku(product.getSku())
                .slug(product.getSlug())
                .description(product.getDescription())

                .originalPrice(product.getOriginalPrice())
                .salePrice(price.getSalePrice())
                .finalPrice(price.getFinalPrice())
                .onSale(price.getOnSale())
                .discountAmount(price.getDiscountAmount())
                .discountType(price.getDiscountType())
                .discountValue(price.getDiscountValue())
                .saleName(price.getSaleName())


                .stockQuantity(product.getStockQuantity())
                .soldQuantity(product.getSoldQuantity())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .brandId(product.getBrand().getId())
                .status(product.getStatus())
                .brandName(product.getBrand().getName())
                .mainImage(mainImage)
                .images(imageResponses)
                .createdAt(product.getCreatedAt())
                .updatedAt(product.getUpdatedAt())
                .build();
    }

    public ProductImageResponse toImageResponse(ProductImage image) {
        return ProductImageResponse.builder()
                .id(image.getId())
                .imageUrl(image.getImageUrl())
                .mainImage(image.getMainImage())
                .sortOrder(image.getSortOrder())
                .build();
    }
    public List<ProductImageResponse> toImageResponses(List<ProductImage> images) {
        if (images == null) {
            return List.of();
        }

        return images.stream()
                .sorted(Comparator.comparing(ProductImage::getSortOrder))
                .map(this::toImageResponse)
                .toList();
    }
}
