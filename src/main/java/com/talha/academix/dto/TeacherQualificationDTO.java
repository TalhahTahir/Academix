package com.talha.academix.dto;

import com.talha.academix.enums.Degree;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherQualificationDTO {

    private Long degreeId;
    @NotNull
    private Long teacherId;
    @NotNull
    private Degree degree;
    @NotBlank
    private String institute;
    @NotNull
    private Integer year;
}
