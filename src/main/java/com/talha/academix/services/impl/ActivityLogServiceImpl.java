package com.talha.academix.services.impl;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.talha.academix.enums.ActivityAction;
import com.talha.academix.model.ActivityLog;
import com.talha.academix.repository.ActivityLogRepo;
import com.talha.academix.services.ActivityLogService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ActivityLogServiceImpl implements ActivityLogService {

    private final ActivityLogRepo activityLogRepo;

    @Override
    public void logAction(Long userId, ActivityAction action, String details) {
        ActivityLog log = new ActivityLog();
        log.setId(userId);
        log.setAction(action);
        log.setDetails(details);
        log.setCreatedAt(LocalDateTime.now());
        activityLogRepo.save(log);
    }
}
