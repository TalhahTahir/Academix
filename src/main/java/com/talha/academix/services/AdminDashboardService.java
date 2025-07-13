package com.talha.academix.services;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.DashboardStatsDTO;
import com.talha.academix.enums.RequestStatus;
import com.talha.academix.enums.Role;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.EnrollmentRepo;
import com.talha.academix.repository.ExamRepo;
import com.talha.academix.repository.TeacherPayoutRequestRepo;
import com.talha.academix.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final CourseRepo courseRepo;
    private final EnrollmentRepo enrollmentRepo;
    private final UserRepo userRepo;
    private final ExamRepo examRepo;
    private final TeacherPayoutRequestRepo salaryRequestRepo;

    public DashboardStatsDTO getPlatformStats() {
        long totalCourses = courseRepo.count();
        long totalEnrollments = enrollmentRepo.count();
        long totalStudents = userRepo.countByRole(Role.STUDENT);
        long totalTeachers = userRepo.countByRole(Role.TEACHER);
        long totalExams = examRepo.count();
        long activeEnrollments = enrollmentRepo.countByCompletionPercentageGreaterThan(0);
        long pendingSalaryRequests = salaryRequestRepo.countByStatus(RequestStatus.PENDING);

        return new DashboardStatsDTO(
                totalCourses,
                totalEnrollments,
                totalStudents,
                totalTeachers,
                totalExams,
                activeEnrollments,
                pendingSalaryRequests
        );
    }
}

