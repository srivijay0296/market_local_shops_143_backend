package com.marketlocalshops.files;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin
public class FileUploadController {

    private final SupabaseStorageService storageService;

    public FileUploadController(SupabaseStorageService storageService) {
        this.storageService = storageService;
    }

    @PostMapping({"/api/files/upload", "/api/upload"})
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "File is empty");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        try {
            String fileUrl = storageService.uploadFile(file);

            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);
            response.put("publicUrl", fileUrl);
            response.put("message", "File uploaded successfully");

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Failed to store file: " + e.getMessage());
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping({"/api/files/upload-multiple", "/api/upload/multiple"})
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        if (files == null || files.length == 0) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "No files provided");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        List<String> urls = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            if (file.isEmpty()) {
                errors.add("Skipped empty file");
                continue;
            }
            try {
                String fileUrl = storageService.uploadFile(file);
                urls.add(fileUrl);
            } catch (IOException e) {
                errors.add("Failed to upload " + file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("urls", urls);
        response.put("errors", errors);
        response.put("message", "Processed " + files.length + " files");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping({"/api/files", "/api/upload"})
    @PreAuthorize("hasAnyRole('SELLER', 'ADMIN')")
    public ResponseEntity<?> deleteFile(@RequestParam("url") String url) {
        if (url == null || url.trim().isEmpty()) {
            Map<String, String> response = new HashMap<>();
            response.put("error", "URL parameter is missing");
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        storageService.deleteFile(url);
        Map<String, String> response = new HashMap<>();
        response.put("message", "File deletion processed");
        return ResponseEntity.ok(response);
    }
}
