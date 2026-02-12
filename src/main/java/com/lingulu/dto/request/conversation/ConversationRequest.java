package com.lingulu.dto.request.conversation;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import com.lingulu.validator.ValidAudio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationRequest {
    @ValidAudio
    private MultipartFile audio;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String conversationId;

}