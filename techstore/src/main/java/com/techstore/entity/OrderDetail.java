package com.techstore.entity;


import com.techstore.enums.DiscountType;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "order_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    private String productName;
    private String productImage;

    @Column(precision = 15, scale = 2)
    private BigDecimal originalPrice;

    @Column(precision = 15, scale = 2)
    private BigDecimal salePrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal finalPrice;

    private Boolean onSale;

    @Column(precision = 15, scale = 2)
    private BigDecimal discountAmount;

    @Enumerated(EnumType.STRING)
    private DiscountType discountType;

    @Column(precision = 15, scale = 2)
    private BigDecimal discountValue;

    private String saleName;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal totalPrice;
}
