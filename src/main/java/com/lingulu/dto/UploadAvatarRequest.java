package com.lingulu.dto;

import org.springframework.web.multipart.MultipartFile;

import com.lingulu.validator.ValidAvatar;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UploadAvatarRequest {

    @NotNull(message = "Avatar file is required")
    @ValidAvatar
    private MultipartFile avatarFile;

}
