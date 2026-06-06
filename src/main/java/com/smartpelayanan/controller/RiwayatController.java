package com.smartpelayanan.controller;

import com.smartpelayanan.dto.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/riwayat-status")
public class RiwayatController {

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<Object>> getRiwayat(@PathVariable String id) {
        return ResponseEntity.ok(ApiResponse.success(Map.of("riwayat", "list-riwayat")));
    }
}
