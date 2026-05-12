package com.talha.academix.services.admindashboard;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AdminDashboardDTO;
import com.talha.academix.enums.Role;
import com.talha.academix.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDashboardQueryService {
    
    private final UserRepo userRepo;

    public AdminDashboardDTO.Users usersSection() {
        return AdminDashboardDTO.Users.builder()
                .totalStudents(userRepo.countByRole(Role.STUDENT))
                .totalTeachers(userRepo.countByRole(Role.TEACHER))
                .build();
    }
}
