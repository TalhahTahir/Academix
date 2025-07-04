package com.talha.academix.dto;

import java.time.LocalDateTime;

import com.talha.academix.enums.ActivityAction;

import lombok.Data;

@Data
public class ActivityLogDTO {
    private Long id;
    private Long userId;
    private ActivityAction action;
    private String details;
    private LocalDateTime createdAt;
}
