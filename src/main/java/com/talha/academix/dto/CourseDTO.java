package com.talha.academix.dto;

import java.math.BigDecimal;

import com.talha.academix.enums.CourseCategory;
import com.talha.academix.enums.CourseState;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long courseid;
    private String coursename;
    private String duration;
    private BigDecimal fees;
    private CourseState state;
    private CourseCategory category;
    private Long teacherid;
}
