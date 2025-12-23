package com.talha.academix.services.AdminDashBoard;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AdminDashBoardDTO;
import com.talha.academix.enums.CourseState;
import com.talha.academix.repository.CourseRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CourseDashboardQueryService {
    private final CourseRepo courseRepo;

    public AdminDashBoardDTO.Courses coursesSection() {
        return AdminDashBoardDTO.Courses.builder()
                .totalCourses(courseRepo.count())
                .launched(courseRepo.countByState(CourseState.LAUNCHED))
                .draft(courseRepo.countByState(CourseState.DRAFT))
                .approved(courseRepo.countByState(CourseState.APPROVED))
                .inDevelopment(courseRepo.countByState(CourseState.IN_DEVELOPMENT))
                .rejected(courseRepo.countByState(CourseState.REJECTED))
                .modified(courseRepo.countByState(CourseState.MODIFIED))
                .disabled(courseRepo.countByState(CourseState.DISABLED))
                .build();
    }
}