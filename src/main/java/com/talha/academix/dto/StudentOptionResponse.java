package com.talha.academix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentOptionResponse {
    private Long id;
    private String text;
    private Long questionId;
}