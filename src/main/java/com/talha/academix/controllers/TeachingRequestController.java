package com.talha.academix.controllers;

import com.talha.academix.dto.TeachingRequestDTO;
import com.talha.academix.model.TeachingRequest.Status;
import com.talha.academix.services.TeachingRequestService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/teaching-requests")
@RequiredArgsConstructor
public class TeachingRequestController {

    private final TeachingRequestService requestService;

    @PostMapping
    public TeachingRequestDTO createRequest(@RequestBody TeachingRequestDTO dto) {
        return requestService.createRequest(dto);
    }

    @PutMapping("/{requestId}/status")
    public TeachingRequestDTO updateRequestStatus(@PathVariable Long requestId, @RequestParam Status status) {
        return requestService.updateRequestStatus(requestId, status);
    }

    @GetMapping("/{requestId}")
    public TeachingRequestDTO getRequestById(@PathVariable Long requestId) {
        return requestService.getRequestById(requestId);
    }

    @GetMapping("/teacher/{teacherId}")
    public List<TeachingRequestDTO> getRequestsByTeacher(@PathVariable Long teacherId) {
        return requestService.getRequestsByTeacher(teacherId);
    }

    @GetMapping("/course/{courseId}")
    public List<TeachingRequestDTO> getRequestsByCourse(@PathVariable Long courseId) {
        return requestService.getRequestsByCourse(courseId);
    }

    @DeleteMapping("/{requestId}")
    public void deleteRequest(@PathVariable Long requestId) {
        requestService.deleteRequest(requestId);
    }
}
