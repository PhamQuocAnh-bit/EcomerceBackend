package com.techstore.service.impl;

import com.techstore.dto.reponse.ChatResponse;
import com.techstore.dto.request.ChatRequest;
import com.techstore.service.AiChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AiChatServiceImpl implements AiChatService {

    @Value("${groq.api-key:${gemini.api-key:}}")
    private String apiKey;

    @Value("${groq.api.url:https://api.groq.com/openai/v1/chat/completions}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public ChatResponse chat(ChatRequest request) {
        if (apiKey == null || apiKey.isEmpty()) {
            return ChatResponse.builder()
                    .reply("Xin lỗi, tính năng AI hiện chưa được cấu hình (Thiếu API Key).")
                    .build();
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            String systemPrompt = "Bạn là trợ lý ảo AI của TechStore, một hệ thống thương mại điện tử chuyên bán đồ công nghệ. " +
                    "Nhiệm vụ của bạn là tư vấn khách hàng, giải đáp thắc mắc về sản phẩm công nghệ ngắn gọn, thân thiện và chuyên nghiệp. " +
                    "Nếu khách hàng hỏi các vấn đề không liên quan đến công nghệ hoặc cửa hàng, hãy từ chối khéo léo.";

            // Build request body for Groq (OpenAI format)
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "llama-3.1-8b-instant"); // Use Llama 3.1 8B model which is very fast and capable
            
            List<Map<String, String>> messages = List.of(
                    Map.of("role", "system", "content", systemPrompt),
                    Map.of("role", "user", "content", request.getMessage())
            );
            requestBody.put("messages", messages);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(apiUrl, entity, Map.class);
            
            if (response.getBody() != null && response.getBody().containsKey("choices")) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (!choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    if (message != null && message.containsKey("content")) {
                        String reply = (String) message.get("content");
                        return ChatResponse.builder().reply(reply).build();
                    }
                }
            }

            return ChatResponse.builder().reply("Xin lỗi, tôi không thể trả lời lúc này.").build();

        } catch (org.springframework.web.client.HttpStatusCodeException e) {
            log.error("Lỗi HTTP từ AI API: {}", e.getResponseBodyAsString(), e);
            String errorMessage = "Đã xảy ra lỗi khi kết nối với hệ thống AI.";
            try {
                Map<String, Object> errorMap = new com.fasterxml.jackson.databind.ObjectMapper().readValue(e.getResponseBodyAsString(), Map.class);
                if (errorMap.containsKey("error")) {
                    Map<String, Object> errorDetails = (Map<String, Object>) errorMap.get("error");
                    if (errorDetails.containsKey("message")) {
                        errorMessage += " Chi tiết: " + errorDetails.get("message");
                    }
                }
            } catch (Exception parseEx) {
                errorMessage += " HTTP Status: " + e.getStatusCode();
            }
            return ChatResponse.builder().reply(errorMessage).build();
        } catch (Exception e) {
            log.error("Lỗi khi gọi AI API: ", e);
            return ChatResponse.builder().reply("Đã xảy ra lỗi khi kết nối với hệ thống AI: " + e.getMessage()).build();
        }
    }
}
