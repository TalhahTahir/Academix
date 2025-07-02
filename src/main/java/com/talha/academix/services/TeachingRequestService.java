package com.talha.academix.services;

import com.talha.academix.dto.TeachingRequestDTO;
import com.talha.academix.model.TeachingRequest.Status;

import java.util.List;

public interface TeachingRequestService {
    TeachingRequestDTO createRequest(TeachingRequestDTO dto);
    TeachingRequestDTO updateRequestStatus(Long requestId, Status status);
    TeachingRequestDTO getRequestById(Long requestId);
    List<TeachingRequestDTO> getRequestsByTeacher(Long teacherId);
    List<TeachingRequestDTO> getRequestsByCourse(Long courseId);
    void deleteRequest(Long requestId);
    List<TeachingRequestDTO> getAllRequests();
}
