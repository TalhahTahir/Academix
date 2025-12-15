package com.talha.academix.services;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.springframework.stereotype.Component;
import com.talha.academix.config.SupabaseConfig;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SupabaseStorageClient {

    private final SupabaseConfig supabaseConfig;

    public String buildUploadUrl(String bucket, String objectKey) {
        // Supabase expects raw path segments; URL encode to be safe
        String encodedKey = URLEncoder.encode(objectKey, StandardCharsets.UTF_8)
                .replace("%2F", "/"); // keep folder slashes
        return supabaseConfig.getUrl() + "/storage/v1/object/" + bucket + "/" + encodedKey;
    }

    // Works only if bucket is public
    public String buildPublicUrl(String bucket, String objectKey) {
        String encodedKey = URLEncoder.encode(objectKey, StandardCharsets.UTF_8)
                .replace("%2F", "/");
        return supabaseConfig.getUrl() + "/storage/v1/object/public/" + bucket + "/" + encodedKey;
    }

    public String getAnonKey() {
        return supabaseConfig.getApiKey();
    }
}