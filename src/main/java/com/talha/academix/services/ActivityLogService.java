package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.ActivityLogDTO;
import com.talha.academix.enums.ActivityAction;

public interface ActivityLogService {
    void logAction(Long userId, ActivityAction action, String details);
    List<ActivityLogDTO> getAllActivityLog();
    List<ActivityLogDTO> getActivityLogByActivityAction(ActivityAction action);
    ActivityLogDTO getActivityLogById(Long id);
    
}