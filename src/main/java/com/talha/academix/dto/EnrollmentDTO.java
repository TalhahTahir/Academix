package com.talha.academix.dto;

import java.time.ZonedDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.talha.academix.enums.EnrollmentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long enrollmentID;
    private Long studentID;
    private Long courseID;
    private ZonedDateTime enrollmentDate;
    private EnrollmentStatus status;
    private double completionPercentage;
    private double marks;
}
