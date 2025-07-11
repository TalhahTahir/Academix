package com.talha.academix.dto;

import java.time.LocalDateTime;

import com.talha.academix.enums.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherPayoutRequestDTO {
    
        private Long id;
    private Long teacherId;
    private Long courseId;
    private RequestStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime processedAt;
    private String adminRemarks;
}
