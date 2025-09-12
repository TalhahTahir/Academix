package com.talha.academix.services.impl;

import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.ActivityLogDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.ActivityLog;
import com.talha.academix.model.User;
import com.talha.academix.repository.ActivityLogRepo;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.ActivityLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepo activityLogRepo;
    private final UserRepo userRepo;
    private final ModelMapper mapper;

    @Override
    public ActivityLogDTO getActivityLogById(Long id) {
        ActivityLog log = activityLogRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Activity Log not found: " + id));
        return mapper.map(log, ActivityLogDTO.class);
    }

    @Override
    public List<ActivityLogDTO> getAllActivityLog() {
        return activityLogRepo.findAll()
                .stream()
                .map(log -> mapper.map(log, ActivityLogDTO.class))
                .toList();
    }

    @Override
    public List<ActivityLogDTO> getActivityLogByActivityAction(ActivityAction action) {
        return activityLogRepo.findByAction(action)
                .stream()
                .map(log -> mapper.map(log, ActivityLogDTO.class))
                .toList();
    }

    @Override
    public void logAction(Long userId, ActivityAction action, String details) {
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));
        ActivityLog log = new ActivityLog();
        log.setUser(user);
        log.setAction(action);
        log.setDetails(details);
        log.setCreatedAt(Instant.now());
        activityLogRepo.save(log);
    }
}