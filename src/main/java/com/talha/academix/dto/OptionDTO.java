package com.talha.academix.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OptionDTO {

    private Long id;
    @NotBlank
    private String text;
    
    private boolean correct;
    @NotNull
    private Long questionId;
}
