package com.lingulu.dto.response.info;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {
    private String username;
    private String avatarUrl;
    private String bio;
}
