package com.talha.academix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamResponse {
    private Long examId;
    private String title;
    private Long courseId;
}