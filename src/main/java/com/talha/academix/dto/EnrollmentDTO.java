package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long enrollmentID;
    private Long studentID;
    private Long courseID;
    private Date enrollmentDate;
    private String status;
    private String grade;
    private Integer rating;
}
