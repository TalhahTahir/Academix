package com.talha.academix.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashboardStatsDTO {
    private long totalCourses;
    private long totalEnrollments;
    private long totalStudents;
    private long totalTeachers;
    private long totalExams;
    private long activeEnrollments;
}
