package com.talha.academix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttemptAnswerDTO {
    private Long id;
    private Long attemptId;
    private Long questionId;
    private Long selectedOptionId;
}
