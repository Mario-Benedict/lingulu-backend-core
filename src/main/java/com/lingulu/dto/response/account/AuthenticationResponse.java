package com.lingulu.dto.response.account;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private Boolean authenticated;
}
