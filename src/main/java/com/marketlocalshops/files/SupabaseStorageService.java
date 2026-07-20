package com.marketlocalshops.files;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Slf4j
public class SupabaseStorageService {

    @Value("${supabase.url:}")
    private String supabaseUrl;

    @Value("${supabase.service-role-key:}")
    private String serviceRoleKey;

    @Value("${supabase.bucket:market-local-shops}")
    private String bucketName;

    private static final String UPLOAD_DIR = "uploads";
    private final RestTemplate restTemplate = new RestTemplate();

    public String uploadFile(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFileName != null && originalFileName.contains(".")) {
            fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
        }
        String cleanName = UUID.randomUUID().toString() + fileExtension;

        // If Supabase keys are provided, upload to cloud storage
        if (supabaseUrl != null && !supabaseUrl.trim().isEmpty() && 
            serviceRoleKey != null && !serviceRoleKey.trim().isEmpty()) {
            try {
                String uploadUrl = String.format("%s/storage/v1/object/%s/%s", 
                        supabaseUrl, bucketName, cleanName);

                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(serviceRoleKey);
                headers.setContentType(MediaType.valueOf(
                        file.getContentType() != null ? file.getContentType() : "application/octet-stream"));

                HttpEntity<byte[]> requestEntity = new HttpEntity<>(file.getBytes(), headers);
                
                ResponseEntity<String> response = restTemplate.exchange(
                        uploadUrl, HttpMethod.POST, requestEntity, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    String publicUrl = String.format("%s/storage/v1/object/public/%s/%s", 
                            supabaseUrl, bucketName, cleanName);
                    log.info("Uploaded file to Supabase Storage: {}", publicUrl);
                    return publicUrl;
                } else {
                    log.warn("Supabase upload returned status: {}. Falling back to local storage.", response.getStatusCode());
                }
            } catch (Exception e) {
                log.error("Failed to upload to Supabase: {}. Falling back to local storage.", e.getMessage());
            }
        }

        // Fallback to local storage
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(cleanName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        String localUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(cleanName)
                .toUriString();
        
        log.info("Uploaded file locally: {}", localUri);
        return localUri;
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty()) return;

        // Check if the URL is a Supabase Storage URL
        if (supabaseUrl != null && !supabaseUrl.trim().isEmpty() && fileUrl.contains(supabaseUrl)) {
            try {
                // Parse out the filename
                String pathSeparator = "/" + bucketName + "/";
                int index = fileUrl.indexOf(pathSeparator);
                if (index != -1) {
                    String fileName = fileUrl.substring(index + pathSeparator.length());
                    String deleteUrl = String.format("%s/storage/v1/object/%s/%s", 
                            supabaseUrl, bucketName, fileName);

                    HttpHeaders headers = new HttpHeaders();
                    headers.setBearerAuth(serviceRoleKey);

                    HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
                    restTemplate.exchange(deleteUrl, HttpMethod.DELETE, requestEntity, String.class);
                    log.info("Deleted file from Supabase Storage: {}", fileUrl);
                }
            } catch (Exception e) {
                log.error("Failed to delete file from Supabase Storage: {}", e.getMessage());
            }
        } else if (fileUrl.contains("/uploads/")) {
            // Local file deletion
            try {
                int index = fileUrl.indexOf("/uploads/");
                if (index != -1) {
                    String fileName = fileUrl.substring(index + "/uploads/".length());
                    Path filePath = Paths.get(UPLOAD_DIR).resolve(fileName);
                    Files.deleteIfExists(filePath);
                    log.info("Deleted local file: {}", filePath);
                }
            } catch (Exception e) {
                log.error("Failed to delete local file: {}", e.getMessage());
            }
        }
    }
}
