package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.TeachingRequestDTO;
import com.talha.academix.enums.RequestStatus;

public interface TeachingRequestService {
    TeachingRequestDTO createRequest(TeachingRequestDTO dto);
    TeachingRequestDTO updateRequestStatus(Long requestId, RequestStatus status);
    TeachingRequestDTO getRequestById(Long requestId);
    List<TeachingRequestDTO> getRequestsByTeacher(Long teacherId);
    List<TeachingRequestDTO> getRequestsByCourse(Long courseId);
    void deleteRequest(Long requestId);
    List<TeachingRequestDTO> getAllRequests();
}
