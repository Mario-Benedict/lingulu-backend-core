package com.lingulu.dto.request.conversation;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import com.lingulu.validator.ValidAudio;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversationRequest {
    @ValidAudio
    private MultipartFile audio;
}