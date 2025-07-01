package com.talha.academix.services.impl;

import com.talha.academix.dto.TeachingRequestDTO;

import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.TeachingRequest;
import com.talha.academix.model.TeachingRequest.Status;
import com.talha.academix.repository.TeachingRequestRepo;
import com.talha.academix.services.TeachingRequestService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TeachingRequestServiceImpl implements TeachingRequestService {

    private final TeachingRequestRepo requestRepo;
    private final ModelMapper modelMapper;

    @Override
    public TeachingRequestDTO createRequest(TeachingRequestDTO dto) {
        TeachingRequest request = modelMapper.map(dto, TeachingRequest.class);
        request = requestRepo.save(request);
        return modelMapper.map(request, TeachingRequestDTO.class);
    }

    @Override
    public TeachingRequestDTO updateRequestStatus(Long requestId, Status status) {
        TeachingRequest existing = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));
        existing.setStatus(status);
        existing = requestRepo.save(existing);
        return modelMapper.map(existing, TeachingRequestDTO.class);
    }

    @Override
    public TeachingRequestDTO getRequestById(Long requestId) {
        TeachingRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));
        return modelMapper.map(request, TeachingRequestDTO.class);
    }

    @Override
    public List<TeachingRequestDTO> getRequestsByTeacher(Long teacherId) {
        List<TeachingRequest> requests = requestRepo.findByTeacherId(teacherId);
        return requests.stream()
                .map(r -> modelMapper.map(r, TeachingRequestDTO.class))
                .toList();
    }

    @Override
    public List<TeachingRequestDTO> getRequestsByCourse(Long courseId) {
        List<TeachingRequest> requests = requestRepo.findByCourseId(courseId);
        return requests.stream()
                .map(r -> modelMapper.map(r, TeachingRequestDTO.class))
                .toList();
    }

    @Override
    public void deleteRequest(Long requestId) {
        TeachingRequest request = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));
        requestRepo.delete(request);
    }
}
