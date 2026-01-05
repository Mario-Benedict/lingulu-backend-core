package com.lingulu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.lingulu.dto.GeminiRequest;
import com.lingulu.dto.GeminiResponse;

import java.util.List;

@Service
public class GeminiService {

    private final WebClient webClient;

    public GeminiService(
            @Value("${gemini.api.url}") String apiUrl,
            @Value("${gemini.api.key}") String apiKey
    ) {
        this.webClient = WebClient.builder()
                .baseUrl(apiUrl)
                .defaultHeader("Content-Type", "application/json")
                .defaultUriVariables(
                        java.util.Map.of("key", apiKey)
                )
                .build();
    }

    public String generateResponse(String userText) {

        String systemPrompt = """
                You are an English conversation partner.
                Respond naturally, briefly, and encourage the user to continue speaking.
                """;

        GeminiRequest request = new GeminiRequest(
                List.of(
                        new GeminiRequest.Content(
                                List.of(
                                        new GeminiRequest.Part(systemPrompt),
                                        new GeminiRequest.Part(userText)
                                )
                        )
                )
        );

        GeminiResponse response = webClient.post()
                .uri(uriBuilder ->
                        uriBuilder.queryParam("key", "{key}").build()
                )
                .bodyValue(request)
                .retrieve()
                .bodyToMono(GeminiResponse.class)
                .block();

        if (response == null
                || response.getCandidates() == null
                || response.getCandidates().isEmpty()) {
            throw new RuntimeException("Gemini response is empty");
        }

        return response.getCandidates()
                .get(0)
                .getContent()
                .getParts()
                .get(0)
                .getText();
    }
}
