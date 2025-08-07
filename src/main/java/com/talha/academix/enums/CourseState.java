package com.talha.academix.enums;

public enum CourseState {
    DRAFT,
    REJECTED,
    MODIFIED, // it could be disabled, rejected one or IN_DEVELOPMENT
    APPROVED,
    IN_DEVELOPMENT,
    LAUNCHED,
    DISABLED
}
// amplement by event driven methods, automation