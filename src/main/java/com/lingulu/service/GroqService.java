package com.lingulu.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

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

    public String chat(String userMessage) {
        Map<String, Object> body = Map.of(
            "model", model,
            "messages", List.of(
                Map.of("role", "system", "content", "You are a friendly English speaking partner."),
                Map.of("role", "user", "content", userMessage)
            ),
            "temperature", 0.7
        );

        return webClient.post()
            .uri(apiUrl)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + apiKey)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .map(res -> {
                List<Map<String, Object>> choices =
                        (List<Map<String, Object>>) res.get("choices");
                Map<String, Object> message =
                        (Map<String, Object>) choices.get(0).get("message");
                return message.get("content").toString();
            })
            .block();
    }
}

