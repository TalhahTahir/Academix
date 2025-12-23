package com.talha.academix.services.AdminDashBoard;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AdminDashBoardDTO;
import com.talha.academix.enums.EnrollmentStatus;
import com.talha.academix.repository.EnrollmentRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EnrollmentDashboardQueryService {
    private final EnrollmentRepo enrollmentRepo;

    public AdminDashBoardDTO.Enrollments enrollmentsSection(){
        return AdminDashBoardDTO.Enrollments.builder()
                .totalEnrollments(enrollmentRepo.count())
                .activeEnrollments(enrollmentRepo.countByCompletionPercentageGreaterThan(0))
                .completedEnrollments(enrollmentRepo.countByStatus(EnrollmentStatus.COMPLETED))
                .inProgressEnrollments(enrollmentRepo.countByStatus(EnrollmentStatus.IN_PROGRESS))
                .build();
    }
}
