package com.talha.academix.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;

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

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name="user_id", nullable=false)

    @Enumerated(EnumType.STRING)
    private ActivityAction action;

    private String details;

    private Instant createdAt;
}
