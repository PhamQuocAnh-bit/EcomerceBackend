package com.techstore.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ChatRequest {
    @NotBlank(message = "Tin nhắn không được để trống")
    private String message;
}
