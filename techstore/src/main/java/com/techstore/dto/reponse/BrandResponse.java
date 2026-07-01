package com.techstore.dto.reponse;


import com.techstore.enums.BrandStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BrandResponse {
    private Long id ;
    private String name ;
    private String description;
    private BrandStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updateAt;

}
