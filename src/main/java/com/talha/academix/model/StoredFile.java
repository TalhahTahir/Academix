package com.talha.academix.model;

import java.time.Instant;

import com.talha.academix.enums.StoredFileStatus;
import com.talha.academix.enums.StoredFileType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "stored_file",
    indexes = {
        @Index(name = "idx_stored_file_content", columnList = "content_id"),
        @Index(name = "idx_stored_file_status", columnList = "status"),
        @Index(name = "idx_stored_file_type", columnList = "type")
    },
    uniqueConstraints = {
        @UniqueConstraint(name = "uq_stored_file_object_key", columnNames = "objectKey")
    }
)
public class StoredFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CHANGED: Course -> Content
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private Content content;

    @Column(nullable = false, length = 255)
    private String bucket;

    @Column(nullable = false, length = 1024)
    private String objectKey;

    @Column(nullable = false, length = 255)
    private String fileName;

    @Column(nullable = false, length = 100)
    private String mimeType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private StoredFileType type; // LECTURE, DOCUMENT, CONTENT_IMAGE

    @Column(nullable = false)
    private Long sizeBytes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StoredFileStatus status; // PENDING, READY, FAILED, DELETED

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    void prePersist() {
        createdAt = Instant.now();
        updatedAt = createdAt;
        if (status == null) status = StoredFileStatus.PENDING;
    }

    @PreUpdate
    void preUpdate() {
        updatedAt = Instant.now();
    }
}