package com.lingulu.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lingulu.dto.response.conversation.AIConversationResponse;
import com.lingulu.entity.Conversation;
import com.lingulu.entity.ConversationMessage;
import com.lingulu.repository.ConversationRepository;

@Service
public class ConversationService {

    private final WhisperService whisperService;
    private final GroqService groqService;
    private final PollyService pollyService;
    private final S3StorageService s3StorageService;
    private final ConversationRepository conversationRepository;

    public ConversationService(
            WhisperService whisperService,
            GroqService groqService,
            PollyService pollyService,
            S3StorageService s3StorageService,
            ConversationRepository conversationRepository
    ) {
        this.whisperService = whisperService;
        this.groqService = groqService;
        this.pollyService = pollyService;
        this.s3StorageService = s3StorageService;
        this.conversationRepository = conversationRepository;
    }

    public AIConversationResponse process(
            MultipartFile audio,
            String conversationId,
            String userId
    ) throws Exception {
        Conversation conversation =
                conversationRepository.findById(conversationId)
                        .orElseGet(() -> {
                            Conversation c = new Conversation();
                            c.setId(conversationId);
                            c.setUserId(userId);
                            c.setCreatedAt(Instant.now());
                            return c;
                        });

        String chatId = UUID.randomUUID().toString();

        // 1. STT
        String userText = whisperService.transcribe(audio);

        // 2. Save user audio
        String userAudioKey =
                "conversations/" + userId + "/" + conversationId + "/user/" + chatId + "/input-audio.wav";

        s3StorageService.uploadMultipartFile(
                audio,
                userAudioKey,
                "chat"
        );

        String userAudioUrl =
                s3StorageService.generatePresignedUrl(userAudioKey);

        // 3. Gemini
        // String aiText = geminiService.generateResponse(userText);
        // 3. Groq
        String aiText = groqService.chat(userText);

        // 4. Polly
        byte[] aiAudioBytes = pollyService.synthesize(aiText);

        String aiAudioKey =
                "conversations/" + userId + "/" + conversationId + "/ai/" + chatId + "/response-audio.mp3";

        // 5. Save AI result
        s3StorageService.uploadBytes(
                aiAudioBytes,
                "audio/mpeg",
                aiAudioKey
        );

        // 6. Pre-signed URL
        String aiAudioUrl =
                s3StorageService.generatePresignedUrl(aiAudioKey);

        conversation.getMessages().add(
                new ConversationMessage(
                        "USER",
                        userAudioUrl,
                        userText,
                        Instant.now()
                )
        );


        // AI bubble
        conversation.getMessages().add(
                new ConversationMessage(
                        "AI",
                        aiAudioUrl,
                        aiText,
                        Instant.now()
                )
        );

        conversation.setUpdatedAt(Instant.now());
        conversationRepository.save(conversation);

        return new AIConversationResponse(
                userText,
                aiText,
                userAudioUrl,
                aiAudioUrl
        );
    }
}