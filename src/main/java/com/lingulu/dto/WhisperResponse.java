package com.lingulu.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WhisperResponse {

    @JsonProperty("text")
    private String text;

    public String getText() {
        return text;
    }
}
