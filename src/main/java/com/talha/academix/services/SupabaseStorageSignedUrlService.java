package com.talha.academix.services;

public interface SupabaseStorageSignedUrlService {
    String createSignedUploadUrl(String bucket, String objectKey, int expiresInSeconds);
    String createSignedDownloadUrl(String bucket, String objectKey, int expiresInSeconds);
}