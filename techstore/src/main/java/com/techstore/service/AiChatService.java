package com.techstore.service;

import com.techstore.dto.reponse.ChatResponse;
import com.techstore.dto.request.ChatRequest;

public interface AiChatService {
    ChatResponse chat(ChatRequest request);
}
