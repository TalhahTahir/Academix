package com.talha.academix.services;

import com.talha.academix.enums.ActivityAction;

public interface ActivityLogService {
    void logAction(Long userId, ActivityAction action, String details);
}