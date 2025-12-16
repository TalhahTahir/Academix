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

        // Response contains { "signedURL": "...", "path":"..." }
        Map resp = webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + supabaseConfig.getServiceRoleKey())
                .header("apikey", supabaseConfig.getServiceRoleKey())
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(Map.class)
                .block();

        String signedUrl = (String) resp.get("signedURL");
        // Supabase returns relative signedURL sometimes; make it absolute if needed:
        if (signedUrl != null && signedUrl.startsWith("/")) {
            signedUrl = supabaseConfig.getUrl() + signedUrl;
        }
        return signedUrl;
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

        String signedUrl = (String) resp.get("signedURL");
        if (signedUrl != null && signedUrl.startsWith("/")) {
            signedUrl = supabaseConfig.getUrl() + signedUrl;
        }
        return signedUrl;
    }

    private String encodeKeepingSlashes(String key) {
        return URLEncoder.encode(key, StandardCharsets.UTF_8).replace("%2F", "/");
    }
}