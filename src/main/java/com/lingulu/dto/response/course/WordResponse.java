package com.lingulu.dto.response.course;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WordResponse {
    private String word;
    private float score;
}
