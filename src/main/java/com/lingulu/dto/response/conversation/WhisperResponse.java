package com.lingulu.dto.response.conversation;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WhisperResponse {

    @JsonProperty("text")
    private String text;

    public String getText() {
        return text;
    }
}
