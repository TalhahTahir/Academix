package com.talha.academix.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.talha.academix.enums.TeacherAccountStatus;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "teacher_accounts")
public class TeacherAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // user (teacher) unique
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "teacher_id", nullable = false, unique = true)
    private User teacher;

    // stripeAccountId (acct_...) unique
    @Column(name = "stripe_account_id", nullable = false, unique = true, length = 64)
    private String stripeAccountId;

    // status enum: PENDING, COMPLETED, RESTRICTED
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private TeacherAccountStatus status = TeacherAccountStatus.PENDING; // default: PENDING, COMPLETED, RESTRICTED

    // timestamps
    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private Instant updatedAt;
}
