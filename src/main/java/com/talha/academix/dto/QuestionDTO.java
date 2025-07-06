package com.talha.academix.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private String text;
    private Long examId;
    private List<Long> optionIds; // Representing options by their IDs
}
