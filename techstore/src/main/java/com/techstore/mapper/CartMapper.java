package com.techstore.mapper;

import com.techstore.dto.reponse.CartItemResponse;
import com.techstore.dto.reponse.CartResponse;
import com.techstore.dto.reponse.ProductPriceResult;
import com.techstore.entity.Cart;
import com.techstore.entity.CartItem;
import com.techstore.entity.Product;
import com.techstore.entity.ProductSale;
import com.techstore.repository.ProductSaleRepository;
import com.techstore.service.ProductPricingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CartMapper {

    private final ProductPricingService productPricingService;
    private final ProductSaleRepository productSaleRepository;

    public CartResponse toResponse(Cart cart) {
        List<CartItem> cartItems = cart.getItems();

        if (cartItems == null || cartItems.isEmpty()) {
            return CartResponse.builder()
                    .id(cart.getId())
                    .items(List.of())
                    .totalItems(0)
                    .totalAmount(BigDecimal.ZERO)
                    .build();
        }

        List<Long> productIds = cartItems.stream()
                .map(item -> item.getProduct().getId())
                .toList();

        List<ProductSale> activeSales =
                productSaleRepository.findActiveSalesByProductIds(productIds);

        Map<Long, List<ProductSale>> saleMap = activeSales.stream()
                .collect(Collectors.groupingBy(ps -> ps.getProduct().getId()));

        List<CartItemResponse> items = cartItems.stream()
                .map(item -> toItemResponse(item, saleMap))
                .toList();

        BigDecimal totalAmount = items.stream()
                .map(CartItemResponse::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        int totalItems = items.stream()
                .mapToInt(CartItemResponse::getQuantity)
                .sum();

        return CartResponse.builder()
                .id(cart.getId())
                .items(items)
                .totalItems(totalItems)
                .totalAmount(totalAmount)
                .build();
    }

    private CartItemResponse toItemResponse(
            CartItem item,
            Map<Long, List<ProductSale>> saleMap
    ) {
        Product product = item.getProduct();

        List<ProductSale> activeSales = saleMap.getOrDefault(
                product.getId(),
                List.of()
        );

        ProductPriceResult price =
                productPricingService.calculatePrice(product, activeSales);

        BigDecimal totalPrice = price.getFinalPrice()
                .multiply(BigDecimal.valueOf(item.getQuantity()));

        String mainImage = product.getImages() == null ? null :
                product.getImages()
                        .stream()
                        .filter(img -> Boolean.TRUE.equals(img.getMainImage()))
                        .findFirst()
                        .map(img -> img.getImageUrl())
                        .orElse(null);

        return CartItemResponse.builder()
                .id(item.getId())
                .productId(product.getId())
                .productName(product.getName())
                .productImage(mainImage)
                .originalPrice(price.getOriginalPrice())
                .salePrice(price.getSalePrice())
                .finalPrice(price.getFinalPrice())
                .onSale(price.getOnSale())
                .discountAmount(price.getDiscountAmount())
                .discountType(price.getDiscountType())
                .discountValue(price.getDiscountValue())
                .saleName(price.getSaleName())
                .quantity(item.getQuantity())
                .totalPrice(totalPrice)
                .build();
    }
}