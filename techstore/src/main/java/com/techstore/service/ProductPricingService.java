package com.techstore.service;

import com.techstore.dto.reponse.ProductPriceResult;
import com.techstore.entity.Product;
import com.techstore.entity.ProductSale;
import com.techstore.entity.Sale;
import com.techstore.enums.DiscountType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class ProductPricingService {

    public ProductPriceResult calculatePrice(Product product, List<ProductSale> activeSales) {
        BigDecimal originalPrice = money(product.getOriginalPrice());

        if (activeSales == null || activeSales.isEmpty()) {
            return noSale(originalPrice);
        }

        BigDecimal bestSalePrice = null;
        Sale bestSale = null;

        for (ProductSale productSale : activeSales) {
            Sale sale = productSale.getSale();

            BigDecimal currentSalePrice = calculateSalePrice(originalPrice, sale);

            if (bestSalePrice == null || currentSalePrice.compareTo(bestSalePrice) < 0) {
                bestSalePrice = currentSalePrice;
                bestSale = sale;
            }
        }

        if (bestSale == null) {
            return noSale(originalPrice);
        }

        BigDecimal discountAmount = originalPrice.subtract(bestSalePrice);

        return ProductPriceResult.builder()
                .originalPrice(originalPrice)
                .salePrice(bestSalePrice)
                .finalPrice(bestSalePrice)
                .onSale(true)
                .discountAmount(money(discountAmount))
                .discountType(bestSale.getDiscountType())
                .discountValue(bestSale.getDiscountValue())
                .saleName(bestSale.getName())
                .build();
    }

    private ProductPriceResult noSale(BigDecimal originalPrice) {
        return ProductPriceResult.builder()
                .originalPrice(originalPrice)
                .salePrice(null)
                .finalPrice(originalPrice)
                .onSale(false)
                .discountAmount(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP))
                .discountType(null)
                .discountValue(null)
                .saleName(null)
                .build();
    }

    private BigDecimal calculateSalePrice(BigDecimal originalPrice, Sale sale) {
        BigDecimal salePrice = originalPrice;

        if (sale.getDiscountType() == DiscountType.PERCENT) {
            BigDecimal discountAmount = originalPrice
                    .multiply(sale.getDiscountValue())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            salePrice = originalPrice.subtract(discountAmount);
        }

        if (sale.getDiscountType() == DiscountType.FIXED_AMOUNT) {
            salePrice = originalPrice.subtract(sale.getDiscountValue());
        }

        if (salePrice.compareTo(BigDecimal.ZERO) < 0) {
            salePrice = BigDecimal.ZERO;
        }

        return money(salePrice);
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}