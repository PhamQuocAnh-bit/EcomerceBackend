package com.techstore.mapper;

import com.techstore.dto.reponse.ProductPriceResult;
import com.techstore.dto.reponse.WishlistResponse;
import com.techstore.entity.Product;
import com.techstore.entity.ProductImage;
import com.techstore.entity.Wishlist;
import org.springframework.stereotype.Component;

@Component
public class WishlistMapper {
    public WishlistResponse toResponse(Wishlist wishlist, ProductPriceResult price) {
        if (wishlist == null) {
            return null;
        }
        Product product = wishlist.getProduct();
        
        String mainImage = null;
        if (product.getImages() != null) {
            mainImage = product.getImages().stream()
                    .filter(img -> Boolean.TRUE.equals(img.getMainImage()))
                    .findFirst()
                    .map(ProductImage::getImageUrl)
                    .orElse(product.getImages().isEmpty() ? null : product.getImages().get(0).getImageUrl());
        }

        return WishlistResponse.builder()
                .id(wishlist.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productSku(product.getSku())
                .productSlug(product.getSlug())
                .originalPrice(product.getOriginalPrice())
                .finalPrice(price != null ? price.getFinalPrice() : product.getOriginalPrice())
                .mainImage(mainImage)
                .createdAt(wishlist.getCreatedAt())
                .build();
    }
}
