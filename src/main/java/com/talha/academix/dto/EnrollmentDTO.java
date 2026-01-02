package com.talha.academix.dto;

import java.time.Instant;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import com.talha.academix.enums.EnrollmentStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentDTO {
    private Long enrollmentId;
    private Long studentId;
    private Long courseId;
    private Instant enrollmentDate;
    private EnrollmentStatus status;
    private double completionPercentage;
    private double marks;
}
