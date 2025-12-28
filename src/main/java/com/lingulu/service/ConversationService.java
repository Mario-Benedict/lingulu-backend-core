package com.lingulu.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lingulu.dto.AIConversationResponse;

@Service
public class ConversationService {

    private final WhisperService whisperService;
    private final GeminiService geminiService;
    private final PollyService pollyService;
    private final S3StorageService s3StorageService;

    public ConversationService(
            WhisperService whisperService,
            GeminiService geminiService,
            PollyService pollyService,
            S3StorageService s3StorageService
    ) {
        this.whisperService = whisperService;
        this.geminiService = geminiService;
        this.pollyService = pollyService;
        this.s3StorageService = s3StorageService;
    }

    public AIConversationResponse process(
            MultipartFile audio,
            String conversationId
    ) throws Exception {

        // 1. STT
        String userText = whisperService.transcribe(audio);

        // 2. Save user audio + text
        s3StorageService.uploadMultipartFile(
                audio,
                "conversations/" + conversationId + "/user/input-audio.wav"
        );

        s3StorageService.uploadBytes(
                userText.getBytes(),
                "text/plain",
                "conversations/" + conversationId + "/user/transcript.txt"
        );

        // 3. Gemini
        String aiText = geminiService.generateResponse(userText);

        // 4. Polly
        byte[] aiAudioBytes = pollyService.synthesize(aiText);

        String aiAudioKey =
                "conversations/" + conversationId + "/ai/response-audio.mp3";

        // 5. Save AI result
        s3StorageService.uploadBytes(
                aiAudioBytes,
                "audio/mpeg",
                aiAudioKey
        );

        s3StorageService.uploadBytes(
                aiText.getBytes(),
                "text/plain",
                "conversations/" + conversationId + "/ai/transcript.txt"
        );

        // 6. Pre-signed URL
        String audioUrl =
                s3StorageService.generatePresignedUrl(aiAudioKey);

        return new AIConversationResponse(
                userText,
                aiText,
                audioUrl
        );
    }
}