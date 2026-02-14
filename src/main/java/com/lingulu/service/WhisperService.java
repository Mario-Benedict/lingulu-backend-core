package com.lingulu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import com.lingulu.dto.response.conversation.WhisperResponse;

@Service
public class WhisperService {

    private final WebClient webClient;

    @Value("${whisper.api.url}")
    private String whisperUrl;

    public WhisperService(
            @Value("${whisper.api.key}") String apiKey
    ) {
        this.webClient = WebClient.builder()
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .build();
    }

    public String transcribe(MultipartFile audioFile) {

        WhisperResponse response = webClient.post()
                .uri(whisperUrl)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(
                        BodyInserters.fromMultipartData("file", audioFile.getResource())
                                .with("model", "whisper-large-v3")
                                .with("language", "en")
                )
                .retrieve()
                .bodyToMono(WhisperResponse.class)
                .block();

        if (response == null || response.getText() == null) {
            throw new RuntimeException("Whisper transcription failed");
        }

        return response.getText();
    }
}