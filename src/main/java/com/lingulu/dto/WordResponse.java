package com.lingulu.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
public class WordResponse {
    private String word;
    private float score;
}
