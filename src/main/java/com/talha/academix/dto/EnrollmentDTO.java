package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

import com.talha.academix.enums.EnrollmentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long enrollmentID;
    private Long studentID;
    private Long courseID;
    private Date enrollmentDate;
    private EnrollmentStatus status;
    private double completionPercentage;
    private double marks;
}
