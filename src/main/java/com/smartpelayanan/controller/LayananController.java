package com.smartpelayanan.controller;

import com.smartpelayanan.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/layanan")
public class LayananController {

    @GetMapping
    public ResponseEntity<ApiResponse<Object>> getAllLayanan(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer kategori) {
        return ResponseEntity.ok(ApiResponse.success(Map.of("data", "list-layanan")));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getLayananById(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(Map.of("layanan", "detail-layanan")));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Object>> createLayanan(@RequestBody Map<String, Object> request) {
        return ResponseEntity.status(201).body(ApiResponse.success(Map.of("nomor", "LAY-12345")));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Object>> updateStatus(
            @PathVariable String id, @RequestBody Map<String, Object> request) {
        return ResponseEntity.ok(ApiResponse.success(Map.of("status", "updated")));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteLayanan(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(null));
    }
}
