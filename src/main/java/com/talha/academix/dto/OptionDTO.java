package com.talha.academix.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {
    private Long id;
    private String text;
    private boolean correct;
    private Long questionId;
}
