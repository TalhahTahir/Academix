package com.talha.academix.services.impl;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.talha.academix.config.SupabaseConfig;
import com.talha.academix.services.SupabaseStorageSignedUrlService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SupabaseStorageSignedUrlServiceImpl implements SupabaseStorageSignedUrlService {

    private final WebClient webClient;
    private final SupabaseConfig supabaseConfig;

 @Override
public String createSignedUploadUrl(String bucket, String objectKey, int expiresInSeconds) {
    String encodedKey = encodeKeepingSlashes(objectKey);
    String url = supabaseConfig.getUrl() + "/storage/v1/object/upload/sign/" + bucket + "/" + encodedKey;

    Map<String, Object> body = Map.of("expiresIn", expiresInSeconds);

    Map resp = webClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseConfig.getServiceRoleKey())
            .header("apikey", supabaseConfig.getServiceRoleKey())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    String signed = extractSignedUrl(resp);
    signed = toAbsoluteSignedUrl(signed);

    if (signed == null || signed.isBlank()) {
        throw new RuntimeException("Supabase returned empty signed upload URL. Response: " + resp);
    }
    return signed;
}

@Override
public String createSignedDownloadUrl(String bucket, String objectKey, int expiresInSeconds) {
    String encodedKey = encodeKeepingSlashes(objectKey);
    String url = supabaseConfig.getUrl() + "/storage/v1/object/sign/" + bucket + "/" + encodedKey;

    Map<String, Object> body = Map.of("expiresIn", expiresInSeconds);

    Map resp = webClient.post()
            .uri(url)
            .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseConfig.getServiceRoleKey())
            .header("apikey", supabaseConfig.getServiceRoleKey())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(body)
            .retrieve()
            .bodyToMono(Map.class)
            .block();

    String signed = extractSignedUrl(resp);
    signed = toAbsoluteSignedUrl(signed);

    if (signed == null || signed.isBlank()) {
        throw new RuntimeException("Supabase returned empty signed download URL. Response: " + resp);
    }
    return signed;
}

    private String extractSignedUrl(Map resp) {
        if (resp == null) {
            throw new RuntimeException("Supabase signed URL response is null");
        }

        Object v = resp.get("signedURL") != null ? resp.get("signedURL")
                : resp.get("signedUrl") != null ? resp.get("signedUrl")
                        : resp.get("signed_url") != null ? resp.get("signed_url")
                                : resp.get("url"); // fallback (rare)

        if (v == null) {
            // if supabase returns error object, show it
            throw new RuntimeException("Supabase did not return signed URL. Response: " + resp);
        }
        return v.toString();
    }

    private String encodeKeepingSlashes(String key) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8).replace("%2F", "/");
    }

    // add this helper method inside the class:

    private String toAbsoluteSignedUrl(String signed) {
        if (signed == null || signed.isBlank())
            return signed;

        // If relative -> make absolute
        if (signed.startsWith("/")) {
            signed = supabaseConfig.getUrl() + signed;
        }

        // FIX: if Supabase returned ...supabase.co/object/... (missing /storage/v1)
        // convert to ...supabase.co/storage/v1/object/...
        String base = supabaseConfig.getUrl();

        String wrongPrefix = base + "/object/";
        String rightPrefix = base + "/storage/v1/object/";

        if (signed.startsWith(wrongPrefix)) {
            signed = rightPrefix + signed.substring(wrongPrefix.length());
        }

        return signed;
    }
}