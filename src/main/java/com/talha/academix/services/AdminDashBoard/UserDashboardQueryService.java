package com.talha.academix.services.AdminDashBoard;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AdminDashBoardDTO;
import com.talha.academix.enums.Role;
import com.talha.academix.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDashboardQueryService {
    
    private final UserRepo userRepo;

    public AdminDashBoardDTO.Users usersSection() {
        return AdminDashBoardDTO.Users.builder()
                .totalStudents(userRepo.countByRole(Role.STUDENT))
                .totalTeachers(userRepo.countByRole(Role.TEACHER))
                .build();
    }
}
