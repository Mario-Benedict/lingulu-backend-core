package com.lingulu.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;


import com.lingulu.dto.request.conversation.GroqMessage;
import com.lingulu.dto.response.conversation.ChatMessageResponse;
import com.lingulu.dto.response.conversation.ConversationHistoryResponse;
import com.lingulu.enums.ConversationRole;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.lingulu.dto.response.conversation.AIConversationResponse;
import com.lingulu.entity.conversation.Conversation;
import com.lingulu.entity.conversation.ConversationMessage;
import com.lingulu.repository.ConversationRepository;

@Service
public class ConversationService {

    private final WhisperService whisperService;
    private final GroqService groqService;
    private final PollyService pollyService;
    private final S3StorageService s3StorageService;
    private final ConversationRepository conversationRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final CloudFrontSigner cloudFrontSigner;
    private static final long CONVERSATION_TTL_HOURS = 24;
    private static final int MEMORY_LIMIT = 20;

    public ConversationService(
            WhisperService whisperService,
            GroqService groqService,
            PollyService pollyService,
            S3StorageService s3StorageService,
            ConversationRepository conversationRepository,
            RedisTemplate<String, String> redisTemplate,
            CloudFrontSigner cloudFrontSigner
    ) {
        this.whisperService = whisperService;
        this.groqService = groqService;
        this.pollyService = pollyService;
        this.s3StorageService = s3StorageService;
        this.conversationRepository = conversationRepository;
        this.redisTemplate = redisTemplate;
        this.cloudFrontSigner = cloudFrontSigner;
    }

    public AIConversationResponse process(
            MultipartFile audio,
            String userId
    ) throws Exception {

        String redisKey = "conversation:user:" + userId;
        String conversationId = redisTemplate.opsForValue().get(redisKey);

        Conversation conversation;

        if (conversationId == null) {
            conversation = Conversation.builder()
                    .userId(userId)
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .messages(new ArrayList<>())
                    .build();

            conversationRepository.save(conversation);
            conversationId = conversation.getId();

            redisTemplate.opsForValue()
                    .set(redisKey, conversationId, CONVERSATION_TTL_HOURS, TimeUnit.HOURS);

        } else {

            conversation = conversationRepository.findById(conversationId)
                    .orElseThrow(() -> new RuntimeException("Conversation not found"));

            if (conversation.getMessages() == null) {
                conversation.setMessages(new ArrayList<>());
            }

            redisTemplate.opsForValue()
                    .set(redisKey, conversationId, CONVERSATION_TTL_HOURS, TimeUnit.HOURS);
        }

        String chatId = UUID.randomUUID().toString();
        Instant now = Instant.now();

        String userText = whisperService.transcribe(audio);

        String userAudioKey =
                "conversations/" + userId + "/" + conversationId +
                        "/user/" + chatId + "/input.wav";

        s3StorageService.uploadMultipartFile(audio, userAudioKey, "chat");

        conversation.getMessages().add(
                new ConversationMessage(
                        ConversationRole.USER,
                        userAudioKey,
                        userText,
                        now
                )
        );

        List<GroqMessage> contextMessages = buildContext(conversation);

        String aiText = groqService.chat(contextMessages);

        byte[] aiAudioBytes = pollyService.synthesize(aiText);

        String aiAudioKey =
                "conversations/" + userId + "/" + conversationId +
                        "/ai/" + chatId + "/response.mp3";

        s3StorageService.uploadBytes(aiAudioBytes, "audio/mpeg", aiAudioKey);

        conversation.getMessages().add(
                new ConversationMessage(
                        ConversationRole.AI,
                        aiAudioKey,
                        aiText,
                        now
                )
        );

        conversation.setUpdatedAt(now);
        conversationRepository.save(conversation);

        String userAudioUrl =
                cloudFrontSigner.generateSignedUrl(userAudioKey);

        String aiAudioUrl =
                cloudFrontSigner.generateSignedUrl(aiAudioKey);

        return AIConversationResponse.builder()
                .conversationId(conversationId)
                .userText(userText)
                .aiText(aiText)
                .userAudioUrl(userAudioUrl)
                .aiAudioUrl(aiAudioUrl)
                .createdAt(now)
                .build();
    }

    private List<GroqMessage> buildContext(Conversation conversation) {

        List<ConversationMessage> allMessages = conversation.getMessages();
        int fromIndex = Math.max(0, allMessages.size() - MEMORY_LIMIT);

        List<ConversationMessage> recentMessages =
                allMessages.subList(fromIndex, allMessages.size());

        List<GroqMessage> context = new ArrayList<>();

        context.add(new GroqMessage(
                "system",
                "You are Lingulu, a friendly English speaking tutor. Keep responses natural and conversational."
        ));

        for (ConversationMessage msg : recentMessages) {
            String role = msg.getRole() == ConversationRole.USER
                    ? "user"
                    : "assistant";

            context.add(new GroqMessage(role, msg.getTranscript()));
        }

        return context;
    }

    public ConversationHistoryResponse loadHistory(String userId) {

        String redisKey = "conversation:user:" + userId;

        String conversationId =
                redisTemplate.opsForValue().get(redisKey);

        if (conversationId == null) {
            return ConversationHistoryResponse.builder()
                    .conversationId(null)
                    .messages(Collections.emptyList())
                    .build();
        }

        Conversation conversation =
                conversationRepository.findById(conversationId)
                        .orElseThrow(() ->
                                new RuntimeException("Conversation not found"));

        List<ConversationMessage> conversationMessages =
                conversation.getMessages() == null
                        ? Collections.emptyList()
                        : conversation.getMessages();

        List<ChatMessageResponse> messages =
                conversationMessages.stream()
                        .map(message -> ChatMessageResponse.builder()
                                .role(message.getRole())
                                .text(message.getTranscript())
                                .audioUrl(
                                        cloudFrontSigner.generateSignedUrl(
                                                message.getAudioKey()
                                        )
                                )
                                .createdAt(message.getCreatedAt())
                                .build()
                        )
                        .toList();

        return ConversationHistoryResponse.builder()
                .conversationId(conversationId)
                .messages(messages)
                .build();
    }

}