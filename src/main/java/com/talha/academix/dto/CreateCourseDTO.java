package com.talha.academix.dto;

import java.math.BigDecimal;

import com.talha.academix.enums.CourseCategory;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseDTO {
    @NotBlank
    private String courseName;
    @NotBlank
    private String duration;
    @NotNull
    private BigDecimal fees;
    @NotNull
    private CourseCategory category;
    @NotNull
    private Long teacherId;
}
