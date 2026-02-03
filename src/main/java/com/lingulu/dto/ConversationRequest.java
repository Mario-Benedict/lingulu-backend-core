package com.lingulu.dto;

import org.springframework.web.multipart.MultipartFile;

import com.lingulu.validator.ValidAudio;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ConversationRequest {
    @ValidAudio
    private MultipartFile audio;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String conversationId;

}