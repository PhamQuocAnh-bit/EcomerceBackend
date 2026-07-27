package com.techstore.mapper;

import com.techstore.dto.reponse.InventoryTransactionResponse;
import com.techstore.entity.InventoryTransaction;
import org.springframework.stereotype.Component;

@Component
public class InventoryTransactionMapper {
    public InventoryTransactionResponse toResponse(InventoryTransaction transaction) {
        if (transaction == null) {
            return null;
        }
        return InventoryTransactionResponse.builder()
                .id(transaction.getId())
                .productId(transaction.getProduct().getId())
                .productName(transaction.getProduct().getName())
                .productSku(transaction.getProduct().getSku())
                .type(transaction.getType())
                .quantity(transaction.getQuantity())
                .referenceCode(transaction.getReferenceCode())
                .note(transaction.getNote())
                .createdAt(transaction.getCreatedAt())
                .build();
    }
}
