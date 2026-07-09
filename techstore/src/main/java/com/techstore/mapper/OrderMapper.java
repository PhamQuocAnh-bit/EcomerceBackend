package com.techstore.mapper;

import com.techstore.dto.reponse.OrderDetailResponse;
import com.techstore.dto.reponse.OrderResponse;
import com.techstore.entity.Order;
import com.techstore.entity.OrderDetail;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderMapper {
    public OrderResponse toResponse(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .province(order.getProvince())
                .district(order.getDistrict())
                .ward(order.getWard())
                .addressDetail(order.getAddressDetail())
                .totalProductAmount(order.getTotalProductAmount())
                .shippingFee(order.getShippingFee())
                .totalAmount(order.getTotalAmount())
                .paymentMethod(order.getPaymentMethod())
                .status(order.getStatus())
                .note(order.getNote())
                .items(toDetailResponses(order.getOrderDetails()))
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();

    }

    private List<OrderDetailResponse> toDetailResponses(List<OrderDetail> details) {
        if (details == null) return List.of();

        return details.stream()
                .map(this::toDetailResponse)
                .toList();
    }

    private OrderDetailResponse toDetailResponse(OrderDetail detail) {
        return OrderDetailResponse.builder()
                .id(detail.getId())
                .productId(detail.getProduct().getId())
                .productName(detail.getProductName())
                .productImage(detail.getProductImage())
                .originalPrice(detail.getOriginalPrice())
                .salePrice(detail.getSalePrice())
                .finalPrice(detail.getFinalPrice())
                .onSale(detail.getOnSale())
                .discountAmount(detail.getDiscountAmount())
                .discountType(detail.getDiscountType())
                .discountValue(detail.getDiscountValue())
                .saleName(detail.getSaleName())
                .quantity(detail.getQuantity())
                .totalPrice(detail.getTotalPrice())
                .build();
    }
}
