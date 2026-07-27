package com.techstore.dto.reponse;

import com.techstore.enums.InventoryTransactionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class InventoryTransactionResponse {
    private Long id;
    private Long productId;
    private String productName;
    private String productSku;
    private InventoryTransactionType type;
    private Integer quantity;
    private String referenceCode;
    private String note;
    private LocalDateTime createdAt;
}
