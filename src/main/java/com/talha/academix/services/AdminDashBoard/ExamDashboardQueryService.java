package com.talha.academix.services.AdminDashBoard;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AdminDashBoardDTO;
import com.talha.academix.repository.ExamRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamDashboardQueryService {
    
    private final ExamRepo examRepo;

    public AdminDashBoardDTO.Exams examsSection(){
        return AdminDashBoardDTO.Exams.builder()
                .totalExams(examRepo.count())
                .build();
    }
}
