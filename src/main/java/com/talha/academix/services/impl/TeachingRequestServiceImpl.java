// TeachingRequestServiceImpl.java
package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.TeachingRequestDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.enums.RequestStatus;
import com.talha.academix.enums.Role;
import com.talha.academix.exception.ForbiddenException;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Course;
import com.talha.academix.model.TeachingRequest;
import com.talha.academix.model.User;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.TeachingRequestRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.ActivityLogService;
import com.talha.academix.services.TeachingRequestService;
import com.talha.academix.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeachingRequestServiceImpl implements TeachingRequestService {
        private final TeachingRequestRepo requestRepo;
        private final UserRepo userRepo;
        private final CourseRepo courseRepo;
        private final ActivityLogService activityLogService;
        private final UserService userService;
        private final ModelMapper mapper;

        @Override
        public TeachingRequestDTO submitRequest(Long teacherId, Long courseId) {
                User teacher = userRepo.findById(teacherId)
                                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + teacherId));
                Course course = courseRepo.findById(courseId)
                                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
                if (!teacher.getRole().equals(Role.TEACHER)) {
                        throw new ForbiddenException("Only teacher can requeest for teaching");
                }
                TeachingRequest req = new TeachingRequest();
                req.setTeacher(teacher);
                req.setCourse(course);
                req.setStatus(RequestStatus.PENDING);
                req = requestRepo.save(req);

                activityLogService.logAction(
                                teacherId,
                                ActivityAction.TEACHING_REQUEST,
                                "Teacher " + teacherId + " requested to teach Course " + courseId);

                return mapper.map(req, TeachingRequestDTO.class);
        }

        @Override
        public TeachingRequestDTO processRequest(Long adminId, Long requestId, RequestStatus status) {
                if (userService.adminValidation(adminId)) {
                        TeachingRequest req = requestRepo.findById(requestId)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Request not found: " + requestId));
                        req.setStatus(status);
                        if (status == RequestStatus.APPROVED) {
                                Course course = req.getCourse();
                                course.setTeacher(req.getTeacher());
                                courseRepo.save(course);
                        }
                        req = requestRepo.save(req);

                        activityLogService.logAction(
                                        req.getTeacher().getUserid(),
                                        ActivityAction.TEACHING_REQUEST,
                                        "Teaching request " + requestId + " processed as " + status);

                        return mapper.map(req, TeachingRequestDTO.class);
                } else
                        throw new ForbiddenException("only Admin can process teaching request");
        }

        @Override
        public List<TeachingRequestDTO> getRequestsByTeacher(Long teacherId) {
                User teacher = userRepo.findById(teacherId)
                                .orElseThrow(() -> new ResourceNotFoundException("Teacher not found: " + teacherId));
                return requestRepo.findByTeacher(teacher).stream()
                                .map(r -> mapper.map(r, TeachingRequestDTO.class))
                                .toList();
        }

        @Override
        public List<TeachingRequestDTO> getRequestsByCourse(Long courseId) {
                Course course = courseRepo.findById(courseId)
                                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));
                return requestRepo.findByCourse(course).stream()
                                .map(r -> mapper.map(r, TeachingRequestDTO.class))
                                .toList();
        }

        @Override
        public List<TeachingRequestDTO> getRequestsByStatus(RequestStatus status) {
                return requestRepo.findByStatus(status).stream()
                                .map(r -> mapper.map(r, TeachingRequestDTO.class))
                                .toList();
        }

        @Override
        public void deleteRequest(Long adminId, Long requestId) {
                if (userService.adminValidation(adminId)) {
                        TeachingRequest req = requestRepo.findById(requestId)
                                        .orElseThrow(() -> new ResourceNotFoundException(
                                                        "Request not found: " + requestId));
                        requestRepo.delete(req);
                }
                else throw new ForbiddenException("only Admin can delete request");
        }

}