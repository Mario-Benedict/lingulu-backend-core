package com.lingulu.dto.request.course;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WordRequest {
    @NotBlank
    private String word;

    @NotBlank
    private float score;
}
