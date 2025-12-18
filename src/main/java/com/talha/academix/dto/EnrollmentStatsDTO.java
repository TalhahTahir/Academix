package com.talha.academix.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EnrollmentStatsDTO {
    private Long enrollmentId;
    private Long courseId;

    private long totalLectures;
    private long completedLectures;

    private long totalDocuments;
    private long completedDocuments;

    private double completionPercentage;
}