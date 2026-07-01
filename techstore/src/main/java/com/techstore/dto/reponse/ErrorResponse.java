package com.techstore.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@NoArgsConstructor
@Data
@AllArgsConstructor
@Builder
public class ErrorResponse {
    private  int status;
    private  String message;
    private LocalDateTime timestamp;
}
