package com.techstore.dto.reponse;


import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AddressResponse {
    private Long id;

    private String receiverName;

    private String receiverPhone;

    private String province;

    private String district;

    private String ward;

    private String addressDetail;

    private Boolean defaultAddress;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
