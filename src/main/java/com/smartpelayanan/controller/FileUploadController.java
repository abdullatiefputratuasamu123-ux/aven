package com.smartpelayanan.controller;

import com.smartpelayanan.dto.ApiResponse;
import com.smartpelayanan.entity.LampiranFile;
import com.smartpelayanan.repository.LampiranFileRepository;
import com.smartpelayanan.utils.JwtUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.*;

@RestController
public class FileUploadController {

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10MB
    private static final Set<String> ALLOWED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "jpg", "jpeg", "png", "gif", "mp4", "avi", "pdf", "doc", "docx"
    ));

    private final LampiranFileRepository lampiranFileRepository;
    private final JwtUtils jwtUtils;

    public FileUploadController(LampiranFileRepository lampiranFileRepository, JwtUtils jwtUtils) {
        this.lampiranFileRepository = lampiranFileRepository;
        this.jwtUtils = jwtUtils;
    }

    private Path getUploadDir() throws IOException {
        Path uploadDir = Paths.get("uploads").toAbsolutePath();
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
        return uploadDir;
    }

    @PostMapping("/api/v1/upload")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "referensiId", required = false) String referensiId,
            @RequestParam(value = "tipeReferensi", required = false) String tipeReferensi) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(ApiResponse.error("File tidak boleh kosong"));
            }
            if (file.getSize() > MAX_FILE_SIZE) {
                return ResponseEntity.badRequest().body(ApiResponse.error("Ukuran file maksimal 10MB"));
            }

            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf('.') + 1).toLowerCase();
            }
            if (!ALLOWED_EXTENSIONS.contains(ext)) {
                return ResponseEntity.badRequest().body(ApiResponse.error(
                        "Tipe file tidak diizinkan. Gunakan: jpg, png, gif, mp4, avi, pdf, doc, docx"));
            }

            String savedName = UUID.randomUUID().toString() + "." + ext;
            Path uploadDir = getUploadDir();
            Path targetPath = uploadDir.resolve(savedName);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);

            LampiranFile lampiran = new LampiranFile();
            lampiran.setNamaFile(originalName);
            lampiran.setNamaTersimpan(savedName);
            lampiran.setTipeFile(file.getContentType());
            lampiran.setUkuranFile(file.getSize());
            lampiran.setUrlFile("/uploads/" + savedName);
            lampiran.setReferensiId(referensiId);
            lampiran.setTipeReferensi(tipeReferensi);
            lampiranFileRepository.save(lampiran);

            Map<String, Object> result = new HashMap<>();
            result.put("id", lampiran.getId());
            result.put("namaFile", originalName);
            result.put("urlFile", "/uploads/" + savedName);
            result.put("tipeFile", file.getContentType());
            result.put("ukuranFile", file.getSize());

            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(ApiResponse.error("Gagal menyimpan file: " + e.getMessage()));
        }
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String filename) {
        try {
            Path filePath = getUploadDir().resolve(filename).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.notFound().build();
            }
            String contentType = Files.probeContentType(filePath);
            if (contentType == null) contentType = "application/octet-stream";
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/api/v1/lampiran/{referensiId}")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getLampiran(@PathVariable String referensiId) {
        List<LampiranFile> files = lampiranFileRepository.findByReferensiId(referensiId);
        List<Map<String, Object>> result = files.stream().map(f -> {
            Map<String, Object> m = new HashMap<>();
            m.put("id", f.getId());
            m.put("namaFile", f.getNamaFile());
            m.put("urlFile", f.getUrlFile());
            m.put("tipeFile", f.getTipeFile());
            m.put("ukuranFile", f.getUkuranFile());
            return m;
        }).toList();
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    @DeleteMapping("/api/v1/lampiran/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLampiran(
            @PathVariable UUID id,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            LampiranFile lampiran = lampiranFileRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("File tidak ditemukan"));
            // Delete physical file
            Path filePath = getUploadDir().resolve(lampiran.getNamaTersimpan()).normalize();
            Files.deleteIfExists(filePath);
            lampiranFileRepository.delete(lampiran);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }
}
