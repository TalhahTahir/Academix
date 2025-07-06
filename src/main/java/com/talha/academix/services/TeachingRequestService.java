package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.TeachingRequestDTO;
import com.talha.academix.enums.RequestStatus;

public interface TeachingRequestService {
    TeachingRequestDTO submitRequest(Long teacherId, Long courseId);
    TeachingRequestDTO processRequest(Long requestId, RequestStatus status);
    List<TeachingRequestDTO> getRequestsByTeacher(Long teacherId);
    List<TeachingRequestDTO> getRequestsByCourse(Long courseId);
    List<TeachingRequestDTO> getRequestsByStatus(RequestStatus status);
    void deleteRequest(Long requestId);
}