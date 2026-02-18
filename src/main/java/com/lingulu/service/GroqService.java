package com.lingulu.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class GroqService {

    @Value("${groq.api.key}")
    private String apiKey;

    @Value("${groq.api.url}")
    private String apiUrl;

    @Value("${groq.model}")
    private String model;

    private final WebClient webClient;

    public GroqService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    public String chat(List<com.lingulu.dto.request.conversation.GroqMessage> messages) {

        List<Map<String, String>> formattedMessages = messages.stream()
                .map(msg -> Map.of(
                        "role", msg.getRole(),
                        "content", msg.getContent()
                ))
                .toList();

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", formattedMessages,
                "temperature", 0.7
        );

        return webClient.post()
                .uri(apiUrl)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                .map(res -> {
                    Object choicesObj = res.get("choices");
                    if (choicesObj instanceof List<?> choices && !choices.isEmpty()) {
                        Object firstChoice = choices.getFirst();
                        if (firstChoice instanceof Map<?, ?> choiceMap) {
                            Object messageObj = choiceMap.get("message");
                            if (messageObj instanceof Map<?, ?> messageMap) {
                                return String.valueOf(messageMap.get("content"));
                            }
                        }
                    }
                    return "";
                })
                .onErrorResume(e -> Mono.just("Lulu is currently unavailable.")) // Safe fallback
                .block();
    }
}