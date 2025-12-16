package com.talha.academix.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talha.academix.config.SupabaseConfig;
import com.talha.academix.dto.SignedDownloadResponseDTO;
import com.talha.academix.dto.SignedUploadInitRequestDTO;
import com.talha.academix.dto.SignedUploadInitResponseDTO;
import com.talha.academix.enums.StoredFileStatus;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.exception.RoleMismatchException;
import com.talha.academix.model.Course;
import com.talha.academix.model.StoredFile;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.StoredFileRepo;
import com.talha.academix.services.CourseService;
import com.talha.academix.services.StoredFileService;
import com.talha.academix.services.SupabaseStorageSignedUrlService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoredFileServiceImpl implements StoredFileService {

    private final StoredFileRepo storedFileRepo;
    private final CourseRepo courseRepo; // CHANGED
    private final CourseService courseService;
    private final SupabaseConfig supabaseConfig;
    private final SupabaseStorageSignedUrlService signedUrlService;

    private static final int DEFAULT_UPLOAD_EXPIRES = 600;  // 10 min
    private static final int DEFAULT_DOWNLOAD_EXPIRES = 600; // 10 min

    @Override
    @Transactional
    public SignedUploadInitResponseDTO initiateSignedUpload(SignedUploadInitRequestDTO req) {

        Course course = courseRepo.findById(req.getCourseId())
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + req.getCourseId()));

        if (!courseService.teacherOwnership(req.getTeacherId(), course.getCourseid())) {
            throw new RoleMismatchException("Only course owner can upload files");
        }

        String bucket = supabaseConfig.getStorage().getBucket();
        if (bucket == null || bucket.isBlank()) {
            throw new RuntimeException("supabase.storage.bucket is not configured");
        }
        if (supabaseConfig.getServiceRoleKey() == null || supabaseConfig.getServiceRoleKey().isBlank()) {
            throw new RuntimeException("supabase.serviceRoleKey is not configured (required for signed URLs)");
        }

       String folder;
if (req.getType() == null) {
    folder = "misc";
} else {
    switch (req.getType()) {
        case LECTURE -> folder = "lectures";
        case DOCUMENT -> folder = "documents";
        case CONTENT_IMAGE -> folder = "content-images";
        default -> folder = "misc";
    }
}
        String safeName = (req.getFileName() == null ? "file" : req.getFileName())
                .replaceAll("[^a-zA-Z0-9._-]", "_");

        // CHANGED folder structure: teacher/course/type/uuid-filename (no content)
        String objectKey = "teacher-" + req.getTeacherId()
                + "/course-" + course.getCourseid()
                + "/" + folder
                + "/" + UUID.randomUUID() + "-" + safeName;

        StoredFile sf = new StoredFile();
        sf.setCourse(course); // CHANGED
        sf.setBucket(bucket);
        sf.setObjectKey(objectKey);
        sf.setFileName(req.getFileName());
        sf.setMimeType(req.getMimeType());
        sf.setType(req.getType());
        sf.setSizeBytes(req.getSizeBytes());
        sf.setStatus(StoredFileStatus.PENDING);
        sf = storedFileRepo.save(sf);

        String signedUploadUrl = signedUrlService.createSignedUploadUrl(bucket, objectKey, DEFAULT_UPLOAD_EXPIRES);

        return SignedUploadInitResponseDTO.builder()
                .storedFileId(sf.getId())
                .bucket(bucket)
                .objectKey(objectKey)
                .signedUploadUrl(signedUploadUrl)
                .expiresIn(DEFAULT_UPLOAD_EXPIRES)
                .build();
    }

    @Override
    @Transactional
    public void markReady(Long storedFileId) {
        StoredFile sf = storedFileRepo.findById(storedFileId)
                .orElseThrow(() -> new ResourceNotFoundException("StoredFile not found: " + storedFileId));
        sf.setStatus(StoredFileStatus.READY);
        storedFileRepo.save(sf);
    }

    @Override
    @Transactional(readOnly = true)
    public SignedDownloadResponseDTO getSignedDownloadUrl(Long storedFileId, int expiresInSeconds) {
        StoredFile sf = storedFileRepo.findById(storedFileId)
                .orElseThrow(() -> new ResourceNotFoundException("StoredFile not found: " + storedFileId));

        int exp = expiresInSeconds > 0 ? expiresInSeconds : DEFAULT_DOWNLOAD_EXPIRES;

        String signedDownloadUrl = signedUrlService.createSignedDownloadUrl(sf.getBucket(), sf.getObjectKey(), exp);

        return SignedDownloadResponseDTO.builder()
                .storedFileId(sf.getId())
                .bucket(sf.getBucket())
                .objectKey(sf.getObjectKey())
                .signedDownloadUrl(signedDownloadUrl)
                .expiresIn(exp)
                .build();
    }
}