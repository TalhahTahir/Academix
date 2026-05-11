package com.talha.academix.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {

    private Long examId;
    @NotBlank
    private String title;
    @NotNull
    private Long courseId;
    @NotEmpty
    private List<QuestionDTO> questions;
}
