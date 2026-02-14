package com.lingulu.dto.request.profile;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import com.lingulu.validator.ValidAvatar;

import jakarta.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UploadAvatarRequest {

    @NotNull(message = "Avatar file is required")
    @ValidAvatar
    private MultipartFile avatarFile;

}
