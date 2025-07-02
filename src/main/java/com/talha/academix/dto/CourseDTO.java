package com.talha.academix.dto;

import com.talha.academix.enums.CourseCatagory;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseDTO {
    private Long courseid;
    private String coursename;
    private String duration;
    private Integer fees;
    private CourseCatagory catagory;
    private Long teacherid;
    private Long examid;
    private Long contentid;
}
