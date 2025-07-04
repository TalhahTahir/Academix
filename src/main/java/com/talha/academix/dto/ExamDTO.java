package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {
    private Long examId;
    private Long courseId;
    private String questions;
}
