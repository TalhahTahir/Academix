package com.talha.academix.services.admindashboard;

import org.springframework.stereotype.Service;

import com.talha.academix.dto.AdminDashboardDTO;
import com.talha.academix.repository.ExamRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ExamDashboardQueryService {
    
    private final ExamRepo examRepo;

    public AdminDashboardDTO.Exams examsSection(){
        return AdminDashboardDTO.Exams.builder()
                .totalExams(examRepo.count())
                .build();
    }
}
