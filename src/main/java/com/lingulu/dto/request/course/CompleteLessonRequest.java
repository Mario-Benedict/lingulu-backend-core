package com.lingulu.dto.request.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompleteLessonRequest {

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String lessonId;
}
