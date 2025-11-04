package com.talha.academix.dto;

import java.math.BigDecimal;

import com.talha.academix.enums.CourseCategory;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateCourseDTO {
    private String coursename;
    private String duration;
    private BigDecimal fees;
    private CourseCategory category;
    private Long teacherid;
}
