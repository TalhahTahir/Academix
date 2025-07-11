package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.TeacherPayoutRequestDTO;

public interface TeacherPayoutRequestService {
    TeacherPayoutRequestDTO requestPayout(Long teacherId, Long courseId);
    List<TeacherPayoutRequestDTO> getAllPendingRequests();
    List<TeacherPayoutRequestDTO> getRequestsByTeacher(Long teacherId);
    TeacherPayoutRequestDTO approveRequest(Long requestId, String adminRemarks);
    TeacherPayoutRequestDTO rejectRequest(Long requestId, String adminRemarks);
}

