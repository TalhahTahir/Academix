package com.talha.academix.services;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AdminDashBoardDTO;
import com.talha.academix.services.AdminDashBoard.CertificateDashboardQueryService;
import com.talha.academix.services.AdminDashBoard.CourseDashboardQueryService;
import com.talha.academix.services.AdminDashBoard.EnrollmentDashboardQueryService;
import com.talha.academix.services.AdminDashBoard.ExamDashboardQueryService;
import com.talha.academix.services.AdminDashBoard.UserDashboardQueryService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminDashboardService {

    private final UserDashboardQueryService userDashboardQueryService;
    private final CourseDashboardQueryService courseDashboardQueryService;
    private final EnrollmentDashboardQueryService enrollmentDashboardQueryService;
    private final ExamDashboardQueryService examDashboardQueryService;
    private final CertificateDashboardQueryService certificateDashboardQueryService;

    public AdminDashBoardDTO getAdminDashboard() {
        return AdminDashBoardDTO.builder()
                .users(userDashboardQueryService.usersSection())
                .courses(courseDashboardQueryService.coursesSection())
                .enrollments(enrollmentDashboardQueryService.enrollmentsSection())
                .exams(examDashboardQueryService.examsSection())
                .certificates(certificateDashboardQueryService.certificatesSection())
                .build();
    }
}
