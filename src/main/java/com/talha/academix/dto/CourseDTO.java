package com.talha.academix.dto;

import com.talha.academix.enums.CourseCatagory;

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
    private Integer fees;
    private Integer salary;
    private CourseCatagory catagory;
    private Long teacherid;
    // Representing collections by IDs or omitted
}
