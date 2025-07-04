package com.talha.academix.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

import com.talha.academix.enums.ActivityAction;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "activity_logs")
public class ActivityLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;  // Nullable if it's a system event

    @Enumerated(EnumType.STRING)
    private ActivityAction action;

    private String details;

    private LocalDateTime createdAt;
}
