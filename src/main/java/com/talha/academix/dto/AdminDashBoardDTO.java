package com.talha.academix.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashBoardDTO {
    private Users users;
    private Courses courses;
    private Exams exams;
    private Enrollments enrollments;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Users {
        private long totalStudents;
        private long totalTeachers;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Courses {
        private long totalCourses;
        private long launched;
        private long draft;
        private long approved;
        private long inDevelopment;
        private long rejected;
        private long modified;
        private long disabled;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Enrollments {
        private long totalEnrollments;
        private long activeEnrollments;
        private long completedEnrollments;
        private long inProgressEnrollments;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Exams {
        private long totalExams;
    }

}