package com.lingulu.dto.request.course;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LessonDetailRequest {
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9-]+$")
    private String lessonId;
}
