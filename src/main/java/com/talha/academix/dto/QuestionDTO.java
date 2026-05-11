package com.talha.academix.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {


    private Long id;
    @NotBlank
    private String text;
    @NotNull
    private Long examId;
    private List<Long> optionIds; // Representing options by their IDs
}
