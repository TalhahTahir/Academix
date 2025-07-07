package com.talha.academix.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExamDTO {
    private Long examId;
    private String title;
    private Long courseId;
    private List<QuestionDTO> questions;
}
