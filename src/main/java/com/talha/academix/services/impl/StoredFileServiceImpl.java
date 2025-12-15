package com.talha.academix.services.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.talha.academix.dto.FileUploadRequestDTO;
import com.talha.academix.dto.FileUploadResponseDTO;
import com.talha.academix.enums.StoredFileStatus;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.Content;
import com.talha.academix.model.Course;
import com.talha.academix.model.StoredFile;
import com.talha.academix.repository.CourseRepo;
import com.talha.academix.repository.StoredFileRepo;
import com.talha.academix.services.SupabaseStorageClient;
import com.talha.academix.services.StoredFileService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StoredFileServiceImpl implements StoredFileService {

    // Change if you want from configuration
    private static final String BUCKET = "academix"; // <-- set your actual bucket name
    private static final boolean BUCKET_PUBLIC = true; // <-- set based on your supabase bucket

    private final StoredFileRepo storedFileRepo;
    private final CourseRepo courseRepo;
    private final SupabaseStorageClient storageClient;

    @Override
    @Transactional
    public FileUploadResponseDTO initiateUpload(Long teacherId, Long courseId, FileUploadRequestDTO req) {
        Course course = courseRepo.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course not found: " + courseId));

        // Minimal ownership check (no Spring Security yet)
        if (course.getTeacher() == null || !course.getTeacher().getUserid().equals(teacherId)) {
            throw new RuntimeException("Teacher does not own this course");
        }

        // We need a Content to attach StoredFile to (your StoredFile model requires content_id).
        // If your course can have multiple contents, you need to decide which Content holds these files.
        // For now: ensure there is at least one content record, or you pass contentId instead.
        // Practical simplest fix: change StoredFile.content -> Course (recommended),
        // but to keep your current model, we will require a single "default content".
        // If you already create content later, then pass contentId instead of courseId.

        // TEMP approach: use first content as "owner" of stored files (must exist!)
        Content content = course.getContents() != null && !course.getContents().isEmpty()
                ? (Content) course.getContents().get(0)
                : null;

        if (content == null) {
            throw new RuntimeException("No Content exists for this course yet. Create Content first, then upload.");
        }

        String folder = req.getType() == null ? "misc"
                : (req.getType().name().equalsIgnoreCase("LECTURE") ? "lectures" : "documents");

        String safeName = req.getFileName() == null ? "file" : req.getFileName().replaceAll("[^a-zA-Z0-9._-]", "_");
        String objectKey = "teacher-" + teacherId
                + "/course-" + courseId
                + "/" + folder
                + "/" + UUID.randomUUID() + "-" + safeName;

        StoredFile sf = new StoredFile();
        sf.setContent(content);
        sf.setBucket(BUCKET);
        sf.setObjectKey(objectKey);
        sf.setFileName(req.getFileName());
        sf.setMimeType(req.getMimeType());
        sf.setType(req.getType());
        sf.setSizeBytes(req.getSizeBytes());
        sf.setStatus(StoredFileStatus.PENDING);

        sf = storedFileRepo.save(sf);

        String uploadUrl = storageClient.buildUploadUrl(BUCKET, objectKey);

        FileUploadResponseDTO.FileUploadResponseDTOBuilder b = FileUploadResponseDTO.builder()
                .storedFileId(sf.getId())
                .bucket(BUCKET)
                .objectKey(objectKey)
                .uploadUrl(uploadUrl)
                .requiredAuthHeaderExample("Authorization: Bearer " + storageClient.getAnonKey());

        // optional: also return public URL if bucket is public
        if (BUCKET_PUBLIC) {
            // You can store this into Lecture.videoUrl / Document.filePath after markReady
            // (or compute again later)
        }

        return b.build();
    }

    @Override
    @Transactional
    public void markReady(Long storedFileId) {
        StoredFile sf = storedFileRepo.findById(storedFileId)
                .orElseThrow(() -> new ResourceNotFoundException("StoredFile not found: " + storedFileId));
        sf.setStatus(StoredFileStatus.READY);
        storedFileRepo.save(sf);
    }
}