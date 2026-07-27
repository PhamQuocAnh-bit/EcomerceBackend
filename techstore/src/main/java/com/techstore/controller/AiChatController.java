package com.techstore.controller;

import com.techstore.dto.reponse.ChatResponse;
import com.techstore.dto.request.ChatRequest;
import com.techstore.service.AiChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
@Tag(name = "AI Chatbox", description = "API giao tiếp với trợ lý ảo AI (Gemini)")
public class AiChatController {

    private final AiChatService aiChatService;

    @PostMapping
    @Operation(summary = "Gửi tin nhắn cho AI", description = "Endpoint công khai để chat với trợ lý ảo TechStore")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(aiChatService.chat(request));
    }
}
