package com.talha.academix.services.impl;

import java.time.Instant;
import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.ActivityLogDTO;
import com.talha.academix.enums.ActivityAction;
import com.talha.academix.model.ActivityLog;
import com.talha.academix.repository.ActivityLogRepo;
import com.talha.academix.services.ActivityLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepo activityLogRepo;
    private final ModelMapper mapper;

    @Override
    public ActivityLogDTO getActivityLogById(Long id) {
        return mapper.map(activityLogRepo.findById(id).orElse(null), ActivityLogDTO.class);
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
        ActivityLog log = new ActivityLog();
        log.setId(userId);
        log.setAction(action);
        log.setDetails(details);
        log.setCreatedAt(Instant.now());
        activityLogRepo.save(log);
    }


}
