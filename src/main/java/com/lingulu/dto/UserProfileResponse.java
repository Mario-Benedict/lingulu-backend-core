package com.lingulu.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String username;
    private String avatarUrl;
    private String bio;
    private String preferredLanguage;
    private String audioPath;
}
