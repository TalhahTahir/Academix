package com.talha.academix.services.impl;

import com.talha.academix.dto.TeachingRequestDTO;
import com.talha.academix.enums.Role;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.TeachingRequest;
import com.talha.academix.model.TeachingRequest.Status;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.TeachingRequestRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.TeachingRequestService;

import lombok.RequiredArgsConstructor;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

import com.talha.academix.exception.AlreadyEnrolledException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Course;
import com.talha.academix.model.User;

@Service
@RequiredArgsConstructor
public class TeachingRequestServiceImpl implements TeachingRequestService {

    private final TeachingRequestRepo requestRepo;
    private final UserRepo userRepo;
    private final CourseRepo courseRepo;
    private final ModelMapper modelMapper;

    @Override
    public TeachingRequestDTO createRequest(TeachingRequestDTO dto) {

        Long userId = dto.getTeacherId();
        User user = userRepo.findById(userId).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        if (!Role.TEACHER.equals(user.getRole())) {
            throw new RoleMismatchException("User is not a teacher");
        }

        Long courseId = dto.getCourseId();
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
        if (course.getTeacherid() != null) {
            throw new AlreadyEnrolledException("This Course has a teacher");
        }

        TeachingRequest request = modelMapper.map(dto, TeachingRequest.class);
        request = requestRepo.save(request);
        return modelMapper.map(request, TeachingRequestDTO.class);
    }

    @Override
    public TeachingRequestDTO updateRequestStatus(Long requestId, Status status) {
        TeachingRequest existing = requestRepo.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Request not found with id: " + requestId));
        existing.setStatus(status);
        requestRepo.save(existing);
        if (Status.APPROVED.equals(status)) {
            Course course = courseRepo.findById(existing.getCourseId()).orElseThrow(
                    () -> new ResourceNotFoundException("Course not found with id: " + existing.getCourseId()));
            course.setTeacherid(existing.getTeacherId());
            courseRepo.save(course);
        }
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

    @Override
    public List<TeachingRequestDTO> getAllRequests() {
        List<TeachingRequest> requests = requestRepo.findAll();
        return requests.stream()
                .map(r -> modelMapper.map(r, TeachingRequestDTO.class))
                .toList();
    }
}
