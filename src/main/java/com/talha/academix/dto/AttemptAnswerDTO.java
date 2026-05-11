package com.talha.academix.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class AttemptAnswerDTO {

    private Long id;
    @NotNull
    private Long attemptId;
    @NotNull
    private Long questionId;
    @NotNull
    private Long selectedOptionId;
}
